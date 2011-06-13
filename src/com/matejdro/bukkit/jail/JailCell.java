package com.matejdro.bukkit.jail;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

public class JailCell {
	private String jailname;
	private String player;
	private Location teleport;
	private Location sign;
	private Location chest;
	private Location chest2;
	
	/**
	 * @param JailName Name of the jail that contains that cell
	 * @param Player Name of the player that is jailed inside that cell. Make it empty string, if there is no such player.
	 */
	public JailCell(String JailName, String Player)
	{
		jailname = JailName;
		player = Player;
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
		return teleport;
	}
	
	/**
	 * Sets Location, where player gets teleported after being jailed in this cell
	 * @param input desired location.
	 */
	public void setTeleportLocation(Location input)
	{
		teleport = input;
	}
	
	/**
	 * Location, where player gets teleported after being jailed in this cell
	 * @param input desired location in string. Format: "x,y,z".
	 */
	public void setTeleportLocation(String input)
	{
		if (input == null || input.trim().equals("")) return;
		String[] str = input.split(",");
		Location loc = new Location(getJail().getTeleportLocation().getWorld(), Double.parseDouble(str[0]), Double.parseDouble(str[1]),Double.parseDouble(str[2]));
		teleport = loc;
	}
		
	/**
	 * @return chest that belongs to this cell. Returns null if there is no such chest.
	 */
	public Chest getChest()
	{
		if (chest == null || chest.getBlock() == null || chest.getBlock().getType() != Material.CHEST) return null;

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
	 * @return second chest (in case of double chest) that belongs to this cell. Returns null if there is no such chest.
	 */
	public Chest getSecondChest()
	{
		if (chest2 == null || chest2.getBlock() == null || chest2.getBlock().getType() != Material.CHEST) return null;

		return (Chest) chest2.getBlock().getState();
	}

	
	/**
	 * set second chest (in case of double chest) that belongs to this cell.
	 * @param input location of the second chest
	 */
	public void setSecondChest(Location input)
	{
		chest2 = input;
	}

	
	/**
	 * second chest (in case of double chest) that belongs to this cell.
	 * @param input location of the second chest in string. Format: "x,y,z"
	 */
	public void setSecondChest(String input)
	{
		if (input == null || input.trim().equals("")) return;
		String[] str = input.split(",");
		Location loc = new Location(getJail().getTeleportLocation().getWorld(), Double.parseDouble(str[0]), Double.parseDouble(str[1]),Double.parseDouble(str[2]));
		chest2 = loc;
	}
	
	/**
	 * @return sign that belongs to this cell. Returns null if there is no such sign.
	 */
	public Sign getSign()
	{
		if (sign == null || sign.getBlock() == null || (sign.getBlock().getType() != Material.SIGN_POST && sign.getBlock().getType() != Material.WALL_SIGN)) return null;
		return (Sign) sign.getBlock().getState();
	}
	
	/**
	 * Set sign that belongs to this chest
	 * @param input location of the sign
	 */
	public void setSign(Location input)
	{
		sign = input;
	}

	
	/**
	 * Set sign that belongs to this chest
	 * @param input location of the sign in string. Format: "x,y,z"
	 */
	public void setSign(String input)
	{
		if (input == null || input.trim().equals("")) return;
		String[] str = input.split(",");
		Location loc = new Location(getJail().getTeleportLocation().getWorld(), Double.parseDouble(str[0]), Double.parseDouble(str[1]),Double.parseDouble(str[2]));
		sign = loc;
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

	
	
}
