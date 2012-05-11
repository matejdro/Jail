package com.matejdro.bukkit.jail;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

public class JailCell {
	private String name;
	private String jailname;
	private String player;
	private Location teleport;
	private HashSet<Location> signs = new HashSet<Location>();
	private Location chest;
	Location oldteleport = null;
	
	/**
	 * @param Name Name of the cell. Can be null if you don't want to specify one.
	 * @param JailName Name of the jail that contains that cell
	 * @param Player Name of the player that is jailed inside that cell. Make it empty string, if there is no such player.
	 */
	public JailCell(String JailName, String Player, String Name)
	{
		jailname = JailName;
		player = Player;
		name = Name;
	}
	
	/**
	 * @return Name of the cell. It can be null or empty if cell does not have a name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Set name of the cell
	 * @param input Name of the cell. Can be null
	 */
	public void setName(String input)
	{
		name = input;
	}
	
	/**
	 * @return Jail zone that this cell is assigned to.
	 */
	public JailZone getJail()
	{
		return Jail.zones.get(jailname);
	}
	
	/**
	 * Set to which jail zone is this cell assigned.
	 * @param input desired jail zone
	 */
	public void setJail(JailZone input)
	{
		jailname = input.getName();
	}
	
	/**
	 * @return Location, where player gets teleported after being jailed in this cell
	 */
	public Location getTeleportLocation()
	{
		if (teleport.getWorld() == null && getJail() != null) teleport.setWorld(getJail().getTeleportLocation().getWorld());
		return teleport;
	}
	
	/**
	 * Sets Location, where player gets teleported after being jailed in this cell
	 * @param input desired location.
	 */
	public void setTeleportLocation(Location input)
	{
		if (oldteleport == null) oldteleport = teleport;
		teleport = input;
	}
	
	/**
	 * Location, where player gets teleported after being jailed in this cell
	 * @param input desired location in string. Format: "x,y,z".
	 */
	public void setTeleportLocation(String input)
	{
		if (input == null || input.trim().equals("")) return;
		if (oldteleport == null) oldteleport = teleport;
		String[] str = input.split(",");
		
		Location loc = new Location(getJail() != null ? getJail().getTeleportLocation().getWorld() : null, Double.parseDouble(str[0]), Double.parseDouble(str[1]),Double.parseDouble(str[2]));
		teleport = loc;
	}
	
	
	/**
	 * @return chest that belongs to this cell. Returns null if there is no such chest.
	 */
	public Chest getChest()
	{
		if (chest == null ) return null;
		if (chest.getWorld() == null) chest.setWorld(getJail().getTeleportLocation().getWorld());
		if (chest.getBlock() == null || (chest.getBlock().getType() != Material.CHEST)) return null;
		
		return (Chest) chest.getBlock().getState();
	}
	
	/**
	 * Set chest that belongs to this cell
	 * @param input location of the chest.
	 */
	public void setChest(Location input)
	{
		chest = input;
	}

	
	/**
	 * Set chest that belongs to this cell
	 * @param input location of the chest in string. Format: "x,y,z".
	 */
	public void setChest(String input)
	{
		if (input == null || input.trim().equals("")) return;
		String[] str = input.split(",");
		Location loc = new Location(getJail().getTeleportLocation().getWorld(), Double.parseDouble(str[0]), Double.parseDouble(str[1]),Double.parseDouble(str[2]));
		chest = loc;
	}
		
	/**
	 * @return a list of signs that belongs to this cell.
	 */
	public ArrayList<Sign> getSigns()
	{
		ArrayList<Sign> list = new ArrayList<Sign>();
		for (Location sign : signs)
		{
			if (sign == null ) continue;
			if (sign.getWorld() == null) sign.setWorld(getJail().getTeleportLocation().getWorld());
			if (sign.getBlock() == null || (sign.getBlock().getType() != Material.SIGN_POST && sign.getBlock().getType() != Material.WALL_SIGN)) continue;
			list.add((Sign) sign.getBlock().getState());
		}
		
		return list;
		
	}
	
	/**
	 * Add sign to this cell
	 * @param input location of the sign
	 */
	public void addSign(Location input)
	{
		signs.add(input);
	}

	
	/**
	 * Add sign to this cell
	 * @param input location of the sign in string. Format: "x,y,z"
	 */
	public void addSign(String input)
	{
		if (input == null || input.trim().equals("")) return;
		String[] str = input.split(",");
		Location loc = new Location(getJail().getTeleportLocation().getWorld(), Double.parseDouble(str[0]), Double.parseDouble(str[1]),Double.parseDouble(str[2]));
		signs.add(loc);
	}
	
	/**
	 * Remove sign from this cell
	 * @param input location of the sign
	 */
	public void removeSign(Location input)
	{
		signs.remove(input);
	}
	
	/**
	 * @return name of the player that is being jailed in that cell.
	 */
	public String getPlayerName()
	{
		return player;
	}
	
	/**
	 * Set name of the player that is being jailed in this cell.
	 * @param input name of the player. Use blank string if you want to clear that field.
	 */
	public void setPlayerName(String input)
	{
		player = input;
	}
	
	/**
	 * Update data of this cell into database.
	 */
	public void update()
	{
		if (oldteleport == null) oldteleport = teleport;
		InputOutput.UpdateCell(this);
		oldteleport = null;
	}
	
	/**
	 * Delete this cell from database
	 */
	public void delete()
	{
		InputOutput.DeleteCell(this);
		if (getJail() != null)
			getJail().getCellList().remove(this);
	}

	
	
}
