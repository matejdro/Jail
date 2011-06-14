package com.matejdro.bukkit.jail;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PrisonerManager {
	public static void PrepareJail(CommandSender sender, String args[])
	{
		String playername;
		int time = -1;
		String jailname = "";
		if (args.length < 1)
		{
			if (sender != null) Util.Message("Usage: /jail [Name] (Time) (Jail Name) (Reason)", sender);
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
		playername = args[0].toLowerCase();
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
			
		if (jailname.equals(Settings.NearestJailCode)) 
			jailname = "";
		
		String jailer;
		if (sender instanceof Player)
			jailer = ((Player) sender).getName();
		else
			jailer = "console";
			
		Player player = Jail.instance.getServer().getPlayer(playername);		
		if (player == null)
		{
			JailPrisoner prisoner = new JailPrisoner(playername, time * 6, jailname, true, "", reason, Settings.AutomaticMute,  "" ,jailer);
			InputOutput.InsertPrisoner(prisoner);
			Jail.prisoners.put(prisoner.getName(), prisoner);
			Util.Message("Player is offline. He will be automatically jailed when he connnects.", sender);
			
		}
		else
		{
			JailPrisoner prisoner = new JailPrisoner(playername, time * 6, jailname, false, "", reason, Settings.AutomaticMute,  "", jailer);
			Jail(prisoner, player);
			Util.Message("Player jailed.", sender);
			
		}
	}
	
	public static void Jail(JailPrisoner prisoner, Player player)
	{
		prisoner.SetBeingReleased(true);
		JailZone jail = prisoner.getJail();
		if (jail == null)
		{
			jail = JailZoneManager.findNearestJail(player.getLocation());
			prisoner.setJail(jail);
		}
		prisoner.setOfflinePending(false);
		if (prisoner.getReason().isEmpty())
			Util.Message(Settings.MessageJail, player);
		else
			Util.Message(Settings.MessageJailReason.replace("<Reason>", prisoner.getReason()), player);

		if (Settings.DeleteInventoryOnJail) player.getInventory().clear();

		
		JailCell cell = jail.getEmptyCell();
		if (cell != null)
		{
			cell.setPlayerName(player.getName());
			prisoner.setCell(cell);
			prisoner.updateSign();
			if (Settings.StoreInventory && cell.getChest() != null)
			{
				Chest chest = cell.getChest();
				chest.getInventory().clear();
				for (int i = 0;i<player.getInventory().getSize();i++)
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
					for (int i = 0;i<player.getInventory().getSize();i++)
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
		
		player.teleport(prisoner.getTeleportLocation());
		if (Settings.StoreInventory) 
		{
			prisoner.storeInventory(player.getInventory());
			player.getInventory().clear();
		}
		
		if (Jail.prisoners.containsKey(prisoner.getName()))
			InputOutput.UpdatePrisoner(prisoner);
		else
			InputOutput.InsertPrisoner(prisoner);
		Jail.prisoners.put(prisoner.getName(), prisoner);
		prisoner.SetBeingReleased(false);
		
	}
	
	public static void UnJail(JailPrisoner prisoner, Player player)
	{
		prisoner.SetBeingReleased(true);
		JailZone jail = prisoner.getJail();	
		Util.Message(Settings.MessageUnjail, player);
		player.teleport(jail.getReleaseTeleportLocation());
		prisoner.SetBeingReleased(false);
		
		JailCell cell = prisoner.getCell();
		if (cell != null)
		{
			if (Settings.StoreInventory && cell.getChest() != null)
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
			if (cell.getSign() != null)
			{
				Sign sign = cell.getSign();
				sign.setLine(0, "");
				sign.setLine(1, "");
				sign.setLine(2, "");
				sign.setLine(3, "");
				sign.update();

			}
			cell.setPlayerName("");
			InputOutput.UpdateCell(cell);
		}
		
		if (Settings.StoreInventory) prisoner.restoreInventory(player);
		prisoner.delete();
	}
	
	public static void PrepareTransferAll(JailZone jail)
	{
		PrepareTransferAll(jail, "find nearest");
	}
	
	public static void PrepareTransferAll(JailZone zone, String target)
	{
		for (JailPrisoner prisoner : zone.getPrisoners())
		{
			prisoner.setTransferDestination(target);
			Player player = Jail.instance.getServer().getPlayer(prisoner.getName());
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
	
	public static void Transfer(JailPrisoner prisoner, Player player)
	{
		if (prisoner.getTransferDestination() == "find nearest") prisoner.setTransferDestination(JailZoneManager.findNearestJail(player.getLocation(), prisoner.getJail().getName()).getName());
		
		if (prisoner.getCell() != null)
		{
			Inventory inventory = player.getInventory();
			JailCell cell = prisoner.getCell();
			cell.setPlayerName("");
			if (cell.getSign() != null)
			{
				Sign sign = cell.getSign();
				sign.setLine(0, "");
				sign.setLine(1, "");
				sign.setLine(2, "");
				sign.setLine(3, "");
				sign.update();
			}
			
			if (cell.getChest() != null) 
			{
				for (ItemStack i: cell.getChest().getInventory().getContents())
					inventory.addItem(i);
				cell.getChest().getInventory().clear();
			}
			if (cell.getSecondChest() != null) 
			{
				for (ItemStack i: cell.getSecondChest().getInventory().getContents())
					inventory.addItem(i);
				cell.getSecondChest().getInventory().clear();
			}
			prisoner.setCell(null);
		}
						
		prisoner.SetBeingReleased(true);
		JailZone jail = Jail.zones.get(prisoner.getTransferDestination());
		prisoner.setJail(jail);
		prisoner.setTransferDestination("");
		prisoner.setOfflinePending(false);
		Util.Message(Settings.MessageTransfer, player);
		Jail.prisoners.put(prisoner.getName(),prisoner);
		
		JailCell cell = jail.getEmptyCell();
		if (cell != null)
		{
			cell.setPlayerName(player.getName());
			prisoner.setCell(cell);
			prisoner.updateSign();
			if (Settings.StoreInventory && cell.getChest() != null)
			{
				Chest chest = cell.getChest();
				chest.getInventory().clear();
				for (int i = 0;i<player.getInventory().getSize();i++)
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
					for (int i = 0;i<player.getInventory().getSize();i++)
					{
						if (chest.getInventory().getSize() <= Util.getNumberOfOccupiedItemSlots(chest.getInventory().getContents())) break;
						if (player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType() == Material.AIR) continue;
						chest.getInventory().addItem(player.getInventory().getItem(i));
						player.getInventory().clear(i);
					}

				}
			}
			InputOutput.UpdateCell(cell);
		}
		
		if (Settings.StoreInventory) 
		{
			prisoner.storeInventory(player.getInventory());
			player.getInventory().clear();
		}
		
		player.teleport(prisoner.getTeleportLocation());
		prisoner.SetBeingReleased(false);
		InputOutput.UpdatePrisoner(prisoner);
	}
}
