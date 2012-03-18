package com.matejdro.bukkit.jail;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;


public class PrisonerManager {
	/**
	 * Parse jail command and prepare user for jailing (if he is online, he will be instantly jailed. Otherwise, he will be jailed first time when he comes online)
	 * @param sender CommandSender that send this command
	 * @param args Arguments for the command. 0 = name, 1 = time, 2 = jail name:cell name, 3 = reason
	 */
    @SuppressWarnings("LoggerStringConcat")
	public static void PrepareJail(CommandSender sender, String args[])
	{
		String playername;
		int time = InputOutput.global.getInt(Setting.DefaultJailTime.getString());
		String jailname = "";
		if (args.length < 1 || (args.length > 1 && (!Util.isInteger(args[1]))) || args[0].trim().equals("?"))
		{
			if (sender != null) Util.Message("Usage: /jail [Name] (Time) (Jail Name:Cell Name) (Reason)", sender);
			return;
		}
		if (Jail.zones.size() < 1)
		{
			if (sender != null) Util.Message("There is no jail available. Build one, before you can jail anyone!", sender);
			return;
		}
		if (Jail.prisoners.containsKey(args[0].toLowerCase()))
		{
			JailPrisoner prisoner = Jail.prisoners.get(args[0].toLowerCase());
			Player player = Jail.instance.getServer().getPlayer(prisoner.getName());
			if (player != null)
			{
				player.teleport(prisoner.getTeleportLocation());
				if (sender != null) Util.Message("Player was teleported back to his jail!", sender);

			}
			else
			{
				if (sender != null) Util.Message("That player is already jailed!", sender);

			}
			return;
		}
		playername = args[0];

		Player player = Jail.instance.getServer().getPlayerExact(playername);		
		if (player == null) player = Jail.instance.getServer().getPlayer(playername);
		if (player != null) playername = player.getName().toLowerCase();
		else if (sender != null)
		{
			Boolean exist = false;
			for (OfflinePlayer p : Bukkit.getServer().getOfflinePlayers())
				if (p.getName().toLowerCase().equals(playername.toLowerCase()))
				{
					exist = true;
					break;
				}
			if (!exist)
			{
				Util.Message("Player " + playername + " was never on this server!", sender);
				return;
			}
		}
		
		if (args.length > 1)
			time = Integer.valueOf(args[1]);
		if (args.length > 2)
			jailname = args[2].toLowerCase();
		String reason = "";
		if (args.length > 3)
		{
			for (int i=3;i<args.length;i++)
			{
				reason+= " " + args[i];
			}
			if (reason.length() > 250)
			{
				if (sender != null) Util.Message("Reason is too long!", sender);
				return;
			}
		}
			
		if (jailname.equals(InputOutput.global.getString(Setting.NearestJailCode.getString()))) 
			jailname = "";
		Util.debug("[Jailing " + playername + "] Requested jail: " + jailname);
		String cellname = null;
		if (jailname.contains(":"))
		{
			cellname = jailname.split(":")[1];
			jailname = jailname.split(":")[0];
			Util.debug("[Jailing " + playername + "] Requested cell: " + cellname);
		}
		String jailer;
		if (sender instanceof Player)
			jailer = ((Player) sender).getName();
		else if (sender == null)
			jailer = "other plugin";
		else
			jailer = "console";
			
		if (player == null)
		{
			JailPrisoner prisoner = new JailPrisoner(playername, time * 6, jailname, cellname, true, "", reason, InputOutput.global.getBoolean(Setting.AutomaticMute.getString(), false),  "" ,jailer, "");
			if (prisoner.getJail() != null)
			{
				Util.debug("[Jailing " + playername + "] Searching for requested cell");
				JailCell cell = prisoner.getJail().getRequestedCell(prisoner);
				if (cell != null && (cell.getPlayerName() == null || cell.getPlayerName().trim().equals("")))
				{
					Util.debug("[Jailing " + playername + "] Found requested cell");
					cell.setPlayerName(prisoner.getName());
					cell.update();
				}
			}
			
			InputOutput.InsertPrisoner(prisoner);
			Jail.prisoners.put(prisoner.getName(), prisoner);
			
			
			Util.Message("Player is offline. He will be automatically jailed when he connnects.", sender);
			
		}
		else
		{
			playername = player.getName().toLowerCase();
			JailPrisoner prisoner = new JailPrisoner(playername, time * 6, jailname, cellname, false, "", reason, InputOutput.global.getBoolean(Setting.AutomaticMute.getString(), false),  "", jailer, "");
			Jail(prisoner, player);
			Util.Message("Player jailed.", sender);
			
		}
		
		//Log jailing into console
		if (InputOutput.global.getBoolean(Setting.LogJailingIntoConsole.getString(), false))
		{
			String times;
			if (time < 0) times = "forever"; else times = "for " + String.valueOf(time) + "minutes";
			
			Jail.log.info("Player " + playername + " was jailed by " + jailer + " " + times);
		}
	}
	
	/**
	 * Performs jailing of specified JailPrisoner. 
	 * If you just want to jail someone, I recommend using JailAPI.jailPlayer, 
	 * because it supports offline jail and it's easier to do.
	 * @param prisoner JailPrisoner class of the new prisoner. Must be already inserted into database
	 * @param player Player that will be teleported
	 */
	public static void Jail(JailPrisoner prisoner, Player player)
	{
		if (!prisoner.getName().equals(player.getName().toLowerCase())) return;
		prisoner.SetBeingReleased(true);
		JailZone jail = prisoner.getJail();
		if (jail == null)
		{
			Util.debug(prisoner, "searching for nearest jail");
			jail = JailZoneManager.findNearestJail(player.getLocation());
			prisoner.setJail(jail);
		}
		if (jail == null)
		{
			Util.Message("You are lucky! Server admin was too lazy to set up jail. Go now!", player);
			Jail.log.info("[Jail] There is no jail to pick! Make sure, you have build at least one jail and at least one jail is set to automatic!");
			return;
		}
		prisoner.setOfflinePending(false);
		if (prisoner.getReason().isEmpty())
			Util.Message(jail.getSettings().getString(Setting.MessageJail), player);
		else
			Util.Message(jail.getSettings().getString(Setting.MessageJailReason).replace("<Reason>", prisoner.getReason()), player);

		if (jail.getSettings().getBoolean(Setting.DeleteInventoryOnJail)) player.getInventory().clear();
		
		prisoner.setPreviousPosition(player.getLocation());
		
		JailCell cell = jail.getRequestedCell(prisoner);
		if (cell == null || (cell.getPlayerName() != null && !cell.getPlayerName().equals("") && !cell.getPlayerName().equals(prisoner.getName()))) 
		{
			Util.debug(prisoner, "No requested cell. searching for empty cell");
			cell = null;
			cell = jail.getEmptyCell();
		}
		if (cell != null)
		{
			Util.debug(prisoner, "Found cell!");
			cell.setPlayerName(player.getName());
			prisoner.setCell(cell);
			player.teleport(prisoner.getTeleportLocation());
			prisoner.updateSign();
			if (jail.getSettings().getBoolean(Setting.StoreInventory) && cell.getChest() != null)
			{
				Chest chest = cell.getChest();
				chest.getInventory().clear();
				for (int i = 0;i<40;i++)
				{
					if (chest.getInventory().getSize() <= Util.getNumberOfOccupiedItemSlots(chest.getInventory().getContents())) break;
					if (player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType() == Material.AIR) continue;
					chest.getInventory().addItem(player.getInventory().getItem(i));
					player.getInventory().clear(i);
				}
								
				if (cell.getSecondChest() != null)
				{
					chest = cell.getSecondChest();
					chest.getInventory().clear();
					for (int i = 0;i<40;i++)
					{
						if (chest.getInventory().getSize() <= Util.getNumberOfOccupiedItemSlots(chest.getInventory().getContents())) break;
						if (player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType() == Material.AIR) continue;
						chest.getInventory().addItem(player.getInventory().getItem(i));
						player.getInventory().clear(i);
					}

				}
			}
			cell.update();
		}
		else
		{
			player.teleport(prisoner.getTeleportLocation());
		}
		
		if (jail.getSettings().getBoolean(Setting.StoreInventory)) 
		{
			prisoner.storeInventory(player.getInventory());
			for (int i = 0;i<40;i++)
			{
				player.getInventory().clear(i);
			}
			
		}
		
		if (jail.getSettings().getBoolean(Setting.SpoutChangeSkin))
			Util.changeSkin(player, jail.getSettings().getString(Setting.SpoutSkinChangeURL));
		
		if (jail.getSettings().getBoolean(Setting.EnableChangingPermissions))
		{
			prisoner.setOldPermissions(Util.getPermissionsGroups(player.getName(), jail.getTeleportLocation().getWorld().getName()));
			Util.setPermissionsGroups(player.getName(), (ArrayList<String>) jail.getSettings().getList(Setting.PrisonersPermissionsGroups), jail.getTeleportLocation().getWorld().getName());
		}
		
		 if (prisoner.getJail().getSettings().getBoolean(Setting.IgnorePrisonersSleepingState))
			 player.setSleepingIgnored(true);

		
		if (Jail.prisoners.containsKey(prisoner.getName()))
			InputOutput.UpdatePrisoner(prisoner);
		else
			InputOutput.InsertPrisoner(prisoner);
		Jail.prisoners.put(prisoner.getName(), prisoner);
		prisoner.SetBeingReleased(false);
		
		for (Object o : jail.getSettings().getList(Setting.ExecutedCommandsOnJail))
		{
			String s = (String) o;
			Server cs = (Server) Jail.instance.getServer();
			CommandSender coms = Jail.instance.getServer().getConsoleSender();
			cs.dispatchCommand(coms,prisoner.parseTags(s));
		}
		
	}
	
	/**
	 * Performs releasing of specified JailPrisoner. 
	 * If you just want to release someone, I recommend using prisoner.release, 
	 * because it supports offline release and it's easier to do.
	 * @param prisoner prisoner that will be released
	 * @param player Player that will be teleported
	 */
	public static void UnJail(JailPrisoner prisoner, Player player)
	{
		prisoner.SetBeingReleased(true);
		JailZone jail = prisoner.getJail();	
		Util.Message(jail.getSettings().getString(Setting.MessageUnJail), player);
		
		Util.changeSkin(player, "");
		
		if (jail.getSettings().getBoolean(Setting.EnableChangingPermissions) && !jail.getSettings().getBoolean(Setting.RestorePermissionsToEscapedPrisoners))
		{
			Util.setPermissionsGroups(player.getName(), prisoner.getOldPermissions(), jail.getTeleportLocation().getWorld().getName());
		}
		
		player.setSleepingIgnored(false);
		
		JailCell cell = prisoner.getCell();
		if (cell != null)
		{
			if (cell.getChest() != null)
			{
				Chest chest = cell.getChest();
				for (int i = 0;i<chest.getInventory().getSize();i++)
				{
					if (chest.getInventory().getItem(i) == null || chest.getInventory().getItem(i).getType() == Material.AIR) continue;
					if (player.getInventory().firstEmpty() == -1)
						player.getWorld().dropItem(player.getLocation(), chest.getInventory().getItem(i));
					else
						player.getInventory().addItem(chest.getInventory().getItem(i));
				}
				chest.getInventory().clear();
				
				if (cell.getSecondChest() != null)
				{
					chest = cell.getSecondChest();
					for (int i = 0;i<chest.getInventory().getSize();i++)
					{
						if (chest.getInventory().getItem(i) == null || chest.getInventory().getItem(i).getType() == Material.AIR) continue;
						if (player.getInventory().firstEmpty() == -1)
							player.getWorld().dropItem(player.getLocation(), chest.getInventory().getItem(i));
						else
							player.getInventory().addItem(chest.getInventory().getItem(i));
					}
					chest.getInventory().clear();

				}
			}
			for (Sign sign : cell.getSigns())
			{
				sign.setLine(0, "");
				sign.setLine(1, "");
				sign.setLine(2, "");
				sign.setLine(3, "");
				sign.update();

			}
			cell.setPlayerName("");
			cell.update();

		}
		
		if (jail.getSettings().getBoolean(Setting.TeleportPrisonerOnRelease)) player.teleport(prisoner.getReleaseTeleportLocation());
		
		prisoner.restoreInventory(player);
		prisoner.delete();
		
		for (Object o : jail.getSettings().getList(Setting.ExecutedCommandsOnRelease))
		{
			String s = (String) o;
			Server cs = (Server) Jail.instance.getServer();
			CommandSender coms = Jail.instance.getServer().getConsoleSender();
			cs.dispatchCommand(coms,prisoner.parseTags(s));
		}
	}
	
	/**
	 * Initiate transfer of every prisoner in specified jail zone to another nearest jail zone
	 */
	public static void PrepareTransferAll(JailZone jail)
	{
		PrepareTransferAll(jail, "find nearest");
	}
	
	/**
	 * Initiate transfer of every prisoner in specified jail zone to another jail zone
	 * @param target Name of the destination jail zone
	 */
	public static void PrepareTransferAll(JailZone zone, String target)
	{
		for (JailPrisoner prisoner : zone.getPrisoners())
		{
			prisoner.setTransferDestination(target);
			Player player = Jail.instance.getServer().getPlayerExact(prisoner.getName());
			if (player == null)
			{
				
				prisoner.setOfflinePending(true);
				InputOutput.UpdatePrisoner(prisoner);
				Jail.prisoners.put(prisoner.getName(), prisoner);
				
			}
			else
			{
				Transfer(prisoner, player);
				
			}
		}
		
	}
	
	/**
	 * Performs transfer of specified JailPrisoner. 
	 * If you just want to transfer someone, I recommend using prisoner.transfer, 
	 * because it supports offline transfer and it's easier to do.
	 * @param prisoner Prisoner that will be transfered
	 * @param player Player that will be teleported
	 */
	public static void Transfer(JailPrisoner prisoner, Player player)
	{
		if ("find nearest".equals(prisoner.getTransferDestination())) prisoner.setTransferDestination(JailZoneManager.findNearestJail(player.getLocation(), prisoner.getJail().getName()).getName());
		
		if (prisoner.getCell() != null)
		{
			Inventory inventory = player.getInventory();
			JailCell cell = prisoner.getCell();
			cell.setPlayerName("");
			for (Sign sign : cell.getSigns())
			{
				sign.setLine(0, "");
				sign.setLine(1, "");
				sign.setLine(2, "");
				sign.setLine(3, "");
				sign.update();
			}
			
			if (cell.getChest() != null) 
			{
				for (ItemStack i: cell.getChest().getInventory().getContents())
				{
					if (i == null || i.getType() == Material.AIR) continue;
					inventory.addItem(i);
				}
				cell.getChest().getInventory().clear();
			}
			if (cell.getSecondChest() != null) 
			{
				for (ItemStack i: cell.getSecondChest().getInventory().getContents())
				{
					if (i == null || i.getType() == Material.AIR) continue;
					inventory.addItem(i);
				}
				cell.getSecondChest().getInventory().clear();
			}
			prisoner.setCell(null);
		}
						
		prisoner.SetBeingReleased(true);
		
		String targetJail = prisoner.getTransferDestination();
		if (targetJail.contains(":"))
		{
			prisoner.setRequestedCell(targetJail.split(":")[1]);
			targetJail = targetJail.split(":")[0];			
		}
		
		JailZone jail = Jail.zones.get(targetJail);
		prisoner.setJail(jail);
		prisoner.setTransferDestination("");
		prisoner.setOfflinePending(false);
		Util.Message(jail.getSettings().getString(Setting.MessageTransfer), player);
		Jail.prisoners.put(prisoner.getName(),prisoner);

		JailCell cell = jail.getRequestedCell(prisoner);
		if (cell == null || (cell.getPlayerName() != null && !cell.getPlayerName().equals("") && !cell.getPlayerName().equals(prisoner.getName()))) 
		{
			cell = null;
			cell = jail.getEmptyCell();
		}
		if (cell != null)
		{
			cell.setPlayerName(player.getName());
			prisoner.setCell(cell);
			player.teleport(prisoner.getTeleportLocation());
			prisoner.updateSign();
			if (jail.getSettings().getBoolean(Setting.StoreInventory) && cell.getChest() != null)
			{
				Chest chest = cell.getChest();
				chest.getInventory().clear();
				for (int i = 0;i<40;i++)
				{
					if (chest.getInventory().getSize() <= Util.getNumberOfOccupiedItemSlots(chest.getInventory().getContents())) break;
					if (player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType() == Material.AIR) continue;
					chest.getInventory().addItem(player.getInventory().getItem(i));
					player.getInventory().clear(i);
				}
								
				if (cell.getSecondChest() != null)
				{
					chest = cell.getSecondChest();
					chest.getInventory().clear();
					for (int i = 0;i<40;i++)
					{
						if (chest.getInventory().getSize() <= Util.getNumberOfOccupiedItemSlots(chest.getInventory().getContents())) break;
						if (player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType() == Material.AIR) continue;
						chest.getInventory().addItem(player.getInventory().getItem(i));
						player.getInventory().clear(i);
					}

				}
			}
			cell.update();
		}
		else
		{
			player.teleport(prisoner.getTeleportLocation());
		}
		
		if (jail.getSettings().getBoolean(Setting.StoreInventory)) 
		{
			prisoner.storeInventory(player.getInventory());
			player.getInventory().clear();
		}
		
		prisoner.SetBeingReleased(false);
		InputOutput.UpdatePrisoner(prisoner);
	}
	
	public static void useJailStick(Player player)
	{
		Boolean enabled = Jail.jailStickToggle.get(player);
		if (enabled == null || !enabled) return;

		if (!InputOutput.global.getBoolean(Setting.EnableJailStick.getString(), false) || !InputOutput.jailStickParameters.containsKey(player.getItemInHand().getTypeId())) return;
		if (!Util.permission(player, "jail.usejailstick." + String.valueOf(player.getItemInHand().getTypeId()), PermissionDefault.OP)) return;
		String[] param = InputOutput.jailStickParameters.get(player.getItemInHand().getTypeId());

		List<Block> targets = player.getLineOfSight(null, Integer.parseInt(param[1]));
		for (Block b : targets)
		{
			for (Player p : Bukkit.getServer().getOnlinePlayers())
			{
				if (p == player) continue;
				if ((b.getLocation().equals(p.getLocation().getBlock().getLocation()) || b.getLocation().equals(p.getEyeLocation().getBlock().getLocation())) && Util.permission(player, "jail.canbestickjailed", PermissionDefault.TRUE))
				{
					String args[] = new String[4];
					args[0] = p.getName();
					args[1] = param[2];
					args[2] = param[3];
					args[3] = param[4];
					PrisonerManager.PrepareJail((CommandSender) player, args); 
				}
			}
		}

	}
}
