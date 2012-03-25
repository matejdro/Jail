package com.matejdro.bukkit.jail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


/**
 * JailPrisoner class stores data about specific prisoner and allows you to modify status of the specified prisoner.
 */
public class JailPrisoner {
	private String name;
	private int remaintime;
	private int afktime;
	private JailZone jail;
	private Boolean offline;
	private String transferDest = "";
	private Boolean releasing = false;
	private Boolean muted = false;
	private String reason = "";
	private JailCell cell;
	private String inventory = "";
	private String jailer = "";
	private HashSet<Wolf> guards = new HashSet<Wolf>();
	private String requestedCell;
	private List<String> oldPermissions = new ArrayList<String>();
	private String previousPositionWorld;
	private Location previousPosition;
	private boolean canSpawnGuards = true;
	
	public JailPrisoner()
	{
		offline = false;
		transferDest = "";
		releasing = false;
	}
	
	/**
	 * @param Name Name of this prisoner (player name)
	 * @param Remaintime Jail time of this prisoner in tens of seconds (1 means 10 seconds)
	 * @param Jail Name of the jail zone, where this prisoner will be jailed
	 * @param Cell Name of the cell inside jail zone that prisoner will be jailed in. Can be empty or null if you don't want to specify cell.
	 * @param Offline Is this prisoner scheduled for any actions such as jailing or transfering when he logs in.
	 * @param TransferDest If this prisoner is waiting for transfer, this contains name of the destination jail zone
	 * @param Reason Reason for jailing this prisoner
	 * @param Muted Should this prisoner be muted (not allowed to chat) in jail?
	 * @param Inventory Inventory string for this prisoner
	 * @param Jailer Who jailed this prisoner
	 */
	public JailPrisoner(String Name, int Remaintime, String Jail, String Cell,  Boolean Offline, String TransferDest, String Reason, Boolean Muted, String Inventory, String Jailer, String Permissions)
	{
		name = Name.toLowerCase();
		remaintime = Remaintime;
		setJail(Jail);
		offline = Offline;
		transferDest = TransferDest;
		muted = Muted;
		reason = Reason;
		inventory = Inventory;
		jailer = Jailer;
		requestedCell = Cell;
		afktime = 0;
		setOldPermissions(Permissions);
	}
		
	/**
	 * @return Name of this prisoner (player name)
	 */
	public String getName()
	{
		return name;
	}
		
	/**
	 * @return Remaining jail time of this prisoner in tens of seconds (1 means 10 seconds)
	 */
	public int getRemainingTime()
	{
		return remaintime;
	}
	
	/**
	 * Sets the remaining jail time of this prisoner
	 * @param input new remaining time in tens of seconds (1 means 10 seconds)
	 */
	public void setRemainingTime(int input)
	{
		remaintime = input ;
		updateSign();
	}
	
	/**
	 * @return remaining jail time of this prisoner in minutes
	 */
	public double getRemainingTimeMinutes()
	{
		return remaintime / 6.0;
	}
	
	/**
	 * Sets the remaining jail time of this prisoner
	 * @param input remaining jail time in minutes
	 */
	public void setRemainingTimeMinutes(double input)
	{
		setRemainingTime((int) Math.round(input * 6.0));
	}
	
	/**
	 * @return jail zone, where this prisoner is jailed
	 */
	public JailZone getJail()
	{
		return jail;
	}
		
	/**
	 * Change jail zone, where this prisoner is jailed. 
	 * This only changes association, it will not do any teleportation, 
	 * but prisoner may get teleported by protection, 
	 * since he have new jail zone defined.
	 * @param input new jail zone
	 */
	public void setJail(JailZone input)
	{
		jail = input;
	}
	
	/**
	 * Change jail zone, where this prisoner is jailed. 
	 * This only changes association, it will not do any teleportation, 
	 * but prisoner may get teleported by protection, 
	 * since he have new jail zone defined.
	 * @param jailname name of the new jail zone
	 */
	public void setJail(String jailname)
	{
		jail = Jail.zones.get(jailname);
	}
	
	/**
	 * @return True if this is prisoner scheduled for any actions such as jailing or transfering when he logs in. Otherwise, return false.
	 */
	public Boolean offlinePending()
	{
		return offline;
	}
	
	/**
	 * When this is enabled, events will trigger when prisoner logs in, thus making things such as offline (un)jailing or transfering possible.
	 * @param input should events be triggered on next log in?
	 */
	public void setOfflinePending(Boolean input)
	{
		offline = input;
	}
	
	/**
	 * @return Destination jail zone, if this prisoner is waiting for transfer.
	 */
	public String getTransferDestination()
	{
		return transferDest;
	}
	
	/**
	 * Change destination jail zone, when this prisoner is waiting for transfer.
	 * @param input 
	 */
	public void setTransferDestination(String input)
	{
		transferDest = input;
	}
	
	/**
	 * @return True, if this prisoner is under transfer or being released. Do not trigger any movement based protections when this is true.
	 */
	public Boolean isBeingReleased()
	{
		return releasing;
	}
	
	/**
	 * This option turns off movement protections when active.
	 * Activate this, when you are teleporting prisoner around and don't want movement protections to kick in.
	 * @param input protection state
	 */
	public void SetBeingReleased(Boolean input)
	{
		releasing = input;
	}
	
	/**
	 * @return reason for jailing that prisoner
	 */
	public String getReason()
	{
		return reason;
	}
	
	/**
	 * Sets reason for jailing this prisoner
	 * @param input new reason
	 */
	public void setReason(String input)
	{
		reason = input;
	}
	
	/**
	 * @return Is this prisoner muted (cannot speak in jail)
	 */
	public Boolean isMuted()
	{
		return muted;
	}
	
	/**
	 * This will mute prisoner, preventing him to speak inside jail. Use that to silence players that are spamming chat from jail.
	 * @param input new mute state. True = muted, False = can speak.
	 */
	public void setMuted(Boolean input)
	{
		muted = input;
	}
	
	/**
	 * @return cell that belongs to this prisoner. Returns null if prisoner does not have its own cell.
	 */
	public JailCell getCell()
	{
		return cell;
	}
	
	/**
	 * Sets cell that belongs to this prisoner. 
	 * @param input new cell that will belong to this prisoner.
	 */
	public void setCell(JailCell input)
	{
		cell = input;
	}
	
	/**
	 * @return Name of the cell that was requested when prisoner was jailed.
	 */
	public String getRequestedCell()
	{
		return requestedCell;
	}
	
	/**
	 * Sets the name of the cell where prisoner will be jailed.
	 * @param input name of the cell.
	 */
	public void setRequestedCell(String input)
	{
		requestedCell = input;
	}
	
	/**
	 * @return Inventory string of this prisoner. Format is: id,amount,durability,data;
	 */
	public String getInventory()
	{
		return inventory;
	}
	
	/**
	 * Sets the inventory string of this prisoner.
	 * @param input New inventory string. Format is: id,amount,durability,data;
	 */
	public void setInventory(String input)
	{
		inventory = input;
	}
	
	/**
	 * @return name of the person who jailed this prisoner
	 */
	public String getJailer()
	{
		return jailer;
	}
	
	
	/**
	 * Sets name of the person who jailed this prisoner
	 * @param input name
	 */
	public void setJailer(String input)
	{
		jailer = input;
	}
	
	/**
	 * @return Editable list of permission groups that will be given to the player after he is released
	 */
	public List<String> getOldPermissions()
	{
		return oldPermissions;
	}
	
	/**
	 * @return List of permission groups that will be given to the player after he is released in String, separated by commas (,)
	 */
	public String getOldPermissionsString()
	{
		String perms = "";
		for (String s : oldPermissions)
			perms += s + ",";
		return perms;
	}
	
	/**
	 * Sets list of permission groups that will be given to the player after he is released
	 * @param permissions String in format "group,group2,group3"
	 */
	public void setOldPermissions(String permissions)
	{
		if (permissions == null) 
			oldPermissions = new ArrayList<String>();
		else
			oldPermissions = Arrays.asList(permissions.split(","));
	}
	
	/**
	 * Sets list of permission groups that will be given to the player after he is released
	 * @param permissions List of com.platymuus.bukkit.permissions.Group
	 */
	public void setOldPermissions(List<String> permissions)
	{
		oldPermissions = permissions;
	}
	
	/**
	 * @return Location, where player will be teleported after triggering move protection
	 * (teleport location of player's jail zone or his cell if he have one) 
	 */
	public Location getTeleportLocation()
	{
		if (getCell() != null)
			return getCell().getTeleportLocation();
		else
			return getJail().getTeleportLocation();
	}
	
	/**
	 * @return Location, where player will be teleported after releasing 
	 * (release location of player's jail zone or his before-jailing location if config is set so) 
	 */
	public Location getReleaseTeleportLocation()
	{
		if (getJail().getSettings().getBoolean(Setting.ReleaseBackToPreviousPosition) && previousPosition != null)
			return getPreviousPosition();
		else
			return getJail().getReleaseTeleportLocation();
	}
	
	/**
	 * @return time, for how much was prisoner AFK of this prisoner in ten of seconds (1 means 10 seconds)
	 */
	public int getAFKTime()
	{
		return afktime;
	}
	
	/**
	 * Sets the time, for how much was prisoner AFK.
	 * @param input new time in tens of seconds (1 means 10 seconds)
	 */
	public void setAFKTime(int input)
	{
		afktime = input ;
	}
	
	/*
	 * @return time, for how much was prisoner AFK of this prisoner in minutes
	 */
	public double getAFKTimeMinutes()
	{
		return afktime / 6.0;
	}
	
	/**
	 * Sets the time, for how much was prisoner AFK.
	 * @param time in minutes
	 */
	public void setAFKTimeMinutes(double input)
	{
		setAFKTime((int) Math.round(input * 6.0));
	}

	
	/**
	 * @return List of guards that belong to this prisoner
	 */
	public HashSet<Wolf> getGuards()
	{
		return guards;
	}
	
	/**
	 * @return Can you spawn guards to this prisoner? This will be false, if server is unable to spawn guards for example due to protection.
	 */
	public Boolean canGuardsBeSpawned()
	{
		return canSpawnGuards;
	}
	
	/**
	 * @param input Set this to false to skip guard spawning for this player and teleport him instead.
	 */
	public void setGuardCanBeSpawned(Boolean input)
	{
		canSpawnGuards = input;
	}
	
	/**
	 * @return Position, where player was before he got jailed.
	 */
	public Location getPreviousPosition()
	{
		if (previousPosition == null) return null;
		if (previousPosition.getWorld() == null) previousPosition.setWorld(Jail.instance.getServer().getWorld(previousPositionWorld));
		return previousPosition;
	}
	
	/**
	 * Sets position, where player was before he got jailed.
	 */
	public void setPreviousPosition(Location pos)
	{
		previousPosition = pos;
	}
	
	/**
	 * Sets position, where player was before he got jailed.
	 * @param input string in format "world,x,y,z"
	 */
	public void setPreviousPosition(String input)
	{
		if (input == null || input.trim().equals("")) return;
		String[] str = input.split(",");
		Location loc = new Location(null , Double.parseDouble(str[1]), Double.parseDouble(str[2]),Double.parseDouble(str[3]));
		previousPositionWorld = str[0];
		previousPosition = loc;
	}
	
	
	
	/**
	 * Spawn guard wolves to this prisoner to kill him
	 * @param num Number of guards to spawn
	 * @param location Spawning location
	 * @param player Player, associated with this JailPrisoner
	 */
	public void spawnGuards(int num, Location location, Player player)
	{
		List<BlockFace> checkedCorners = new ArrayList<BlockFace>();					
		for (int i = 0; i < num; i++)
		{
			Location spawn = null;
			for (int ci = 0; ci < 4; ci++)
			{
				Block block = location.getBlock().getRelative(BlockFace.values()[ci]);
				if (!checkedCorners.contains(BlockFace.values()[ci]) && (block.getType() == Material.AIR || block.getType() == Material.STATIONARY_WATER || block.getType() == Material.WATER))
				{
					spawn = block.getLocation();
					checkedCorners.add(BlockFace.values()[ci]);
					break;
				}

			}
			if (spawn == null)
			{
				checkedCorners.clear();
				for (int ci = 0; ci < 3; ci++)
				{
					if (!checkedCorners.contains(BlockFace.values()[ci]) && location.getBlock().getRelative(BlockFace.values()[ci]).getType() == Material.AIR || location.getBlock().getRelative(BlockFace.values()[ci]).getType() == Material.STATIONARY_WATER)
					{
						spawn = location.getBlock().getRelative(BlockFace.NORTH).getLocation();
						checkedCorners.add(BlockFace.values()[ci]);
					}

				}
				if (spawn == null) spawn = location;
			}
			
									
			Wolf guard = (Wolf) location.getWorld().spawnCreature(spawn, CreatureType.WOLF);
			
			
			if (!(guard.getWorld().getEntities().contains(guard)))
			{
				canSpawnGuards=false;
				return;
			}
			
			int health = getJail().getSettings().getInt(Setting.GuardHealth);
			if (health > guard.getMaxHealth())
			{
				Jail.log.warning("[Jail] Guard health cannot be more than " + guard.getMaxHealth() + "! Use Armor to increase toughness of your guards!");
				health = guard.getMaxHealth();
			}
			
			guard.setHealth(health);
			guard.setAngry(true);
			guard.setSitting(false);
			guard.setTarget(player);
						
			getGuards().add(guard);
			Jail.guards.put(guard, this);
		}
	}
	
	/**
	 * Converts specified inventory into inventory string and stores it. Previous inventory string will be deleted. 
	 * @param playerinv inventory that will be stored
	 */
	public void storeInventory(PlayerInventory playerinv)
	{
		String inv = "";
		for (int i = 0;i<40;i++)
		{
			ItemStack item = playerinv.getItem(i);
			if (item == null || item.getType() == Material.AIR) continue;
			
			String enchantString = "";
			for (Entry<Enchantment, Integer> e : item.getEnchantments().entrySet())
				enchantString += String.valueOf(e.getKey().getId()) + ":" + String.valueOf(e.getValue()) + "*";
			if (enchantString.length() > 1) enchantString = enchantString.substring(0, enchantString.length() - 1);
					
			inv += String.valueOf(item.getTypeId()) + "," + String.valueOf(item.getAmount()) + "," +String.valueOf(item.getDurability()) + "," + enchantString + ";";
		}
		inventory = inv;
		InputOutput.UpdatePrisoner(this);
	}
	
	/**
	 * Restores items from inventory string to specified player. Inventory string will be deleted.
	 * @param player player that will receive items
	 */
	public void restoreInventory(Player player)
	{
		if (inventory == null) return;
		String[] inv = inventory.split(";");
		for (String i : inv)
		{
			if (i == null || i.trim().equals("")) return;
			String[] items = i.split(",");
			ItemStack item = new ItemStack(Integer.parseInt(items[0]),Integer.parseInt(items[1]));
			item.setDurability(Short.parseShort(items[2]));
			if (items.length > 3 && items[3].contains(":"))
			{
				String[] enchantments = items[3].split("\\*");
				for (String e : enchantments)
				{
					item.addEnchantment(Enchantment.getById(Integer.parseInt(e.split(":")[0])), Integer.parseInt(e.split(":")[1]));
				}
			}
			
			if (player.getInventory().firstEmpty() == -1)
				player.getWorld().dropItem(player.getLocation(), item);
			else
				player.getInventory().addItem(item);
		}
		inventory = "";
		InputOutput.UpdatePrisoner(this);
	}
	
	/**
	 * Update text of the sign that belongs to prisoner's cell if he have one.
	 */
	public void updateSign()
	{
		if (cell != null) 
		{
			for (Sign sign : cell.getSigns())
			{
				String set = getJail().getSettings().getString(Setting.SignText) ;
				set = set.replaceAll("\\&([0-9abcdef])", "§$1");
				String[] lines = set.split("\\[NEWLINE\\]");
				int max = lines.length;
				if (max > 4) max = 4;
				
				for (int i = 0;i<max;i++)
				{
					sign.setLine(i, parseTags(lines[i]));
					
				}
				sign.update();
			}			
		}
	}
	
	/**
	 * Parse <Player>, <Reason>, <Jailer>, <Jail>, <Time>, <TimeS>, <Cell> for actual values
	 * @param str input string
	 * @return parsed string
	 */
	public String parseTags(String str)
	{
		str = str.replace("<Player>", getName());
		str = str.replace("<Reason>", getReason());
		str = str.replace("<Jailer>", getJailer());
		str = str.replace("<Jail>", getJail().getName());
		
		double time = getRemainingTimeMinutes();
		String tim;
		if (time >= 1.0 || time < 0.0)
			tim = String.valueOf((int) Math.round( time ) * 1);
		else
			tim = String.valueOf(Math.round( time * 10.0d ) / 10.0d);
		str = str.replace("<Time>", tim);
				
		if (getCell() == null || getCell().getName() == null)
			str = str.replace("<Cell>", "");
		else
			str = str.replace("<Cell>", getCell().getName());
		
		if (str.contains("<TimeS>"))
		{
			if (time > -1)
				str = str.replace("<TimeS>", parseTags(getJail().getSettings().getString(Setting.MessageMinutes).replace("<TimeS>", "")));
			else
				str = str.replace("<TimeS>", parseTags(getJail().getSettings().getString(Setting.MessageForever).replace("<TimeS>", "")));		
		}
			
		return str;
	}

	/**
	 * Initiate release procedure. 
	 * If player is online, he will be released instantly, 
	 * otherwise he will be released when he logs in.
	 */
	public void release()
	{
		Player player = Jail.instance.getServer().getPlayerExact(getName());
		
		if (player == null)
		{
			
			setOfflinePending(true);
			setRemainingTime(0);
			update();
			Jail.prisoners.put(getName(), this);
			
		}
		else
		{
			PrisonerManager.UnJail(this, player);
			
		}

	}
	
	/**
	 * Transfer prisoner to next nearest jail. 
	 * If player is online, he will be transfered instantly, 
	 * otherwise he will be transfered when he logs in.
	 */
	public void transfer()
	{
		transfer(null);
	}
	
	/**
	 * Transfer prisoner to specified jail. 
	 * If player is online, he will be transfered instantly, 
	 * otherwise he will be transfered when he logs in.
	 * @param targetjail Name of the destination jail zone
	 */
	public void transfer(String targetjail)
	{
		if (targetjail == null) 
			targetjail = "find nearest";
				
		setTransferDestination(targetjail);
		Player player = Jail.instance.getServer().getPlayerExact(getName());
		
		if (player == null)
		{
			setOfflinePending(true);
			update();
			Jail.prisoners.put(getName(), this);
		}
		else
		{
			PrisonerManager.Transfer(this, player);
		}

	}
	
	/**
	 * Update data of this prisoner into database. 
	 */
	public void update()
	{
		InputOutput.UpdatePrisoner(this);
	}
	
	/**
	 * Delete prisoner from the database. No teleporting is involved.
	 */
	public void delete()
	{
		SetBeingReleased(true);
		JailCell cell = getCell();
		InputOutput.DeletePrisoner(this);
		Jail.prisoners.remove(getName());
		if (cell != null) 
		{
			for (Sign sign : cell.getSigns())
			{
				sign.setLine(0, "");
				sign.setLine(1, "");
				sign.setLine(2, "");
				sign.setLine(3, "");
				sign.update();

			}
			if (cell.getChest() != null) cell.getChest().getInventory().clear();
			if (cell.getSecondChest() != null) cell.getSecondChest().getInventory().clear();
			cell.setPlayerName("");
		}
		
		for (LivingEntity e : guards)
		{
			e.remove();
			Jail.guards.remove(e);
		}
			
		if (getJail() != null && jail.getSettings().getBoolean(Setting.EnableChangingPermissions) && jail.getSettings().getBoolean(Setting.RestorePermissionsToEscapedPrisoners))
		{
			Util.setPermissionsGroups(getName(), getOldPermissions(), jail.getTeleportLocation().getWorld().getName());
		}

	}
	
	}
