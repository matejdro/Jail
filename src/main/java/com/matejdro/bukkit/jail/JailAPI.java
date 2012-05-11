package com.matejdro.bukkit.jail;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Collection of useful methods to easily access and mess with Jail API.
 */
public class JailAPI {	
	/**
	 * Jail specified player
	 * @param playerName Name of the player you want to jail
	 * @param time Jail time in minutes
	 * @param jailName Name of the jail, where prisoner will be jailed. Use null to let plugin select nearest jail.
	 * @param reason Reason for jailing. Use null if you don't want to specify reason.
	 * @deprecated use jailPlayer(String playerName, int time, String jailName, String cellName, String reason)
	 */
	@Deprecated
	public void jailPlayer(String playerName, int time, String jailName, String reason)
	{
		jailPlayer(playerName, time, jailName, "", reason);
	}
	
	/**
	 * Jail specified player
	 * @param playerName Name of the player you want to jail
	 * @param time Jail time in minutes
	 * @param jailName Name of the jail, where prisoner will be jailed. Use null to let plugin select nearest jail.
	 * @param cellName Name of the cell, where prisoner will be jailed.
	 * @param reason Reason for jailing. Use null if you don't want to specify reason.
	 */
	public JailPrisoner jailPlayer(String playerName, int time, String jailName, String cellName, String reason)
	{
		return jailPlayer(playerName, time, jailName, cellName, reason, "other player");
	}
	
	/**
	 * Jail specified player
	 * @param playerName Name of the player you want to jail
	 * @param time Jail time in minutes
	 * @param jailName Name of the jail, where prisoner will be jailed. Use null to let plugin select nearest jail.
	 * @param cellName Name of the cell, where prisoner will be jailed.
	 * @param reason Reason for jailing. Use null if you don't want to specify reason.
	 * @param jailer Who jailed this player? Usually name of your plugin.
	 */

	public JailPrisoner jailPlayer(String playerName, int time, String jailName, String cellName, String reason, String jailer)
	{
		if (jailName == null) jailName = "";
		if (cellName == null) cellName = "";
		if (reason == null) reason = "";
		
		JailPrisoner prisoner = new JailPrisoner(playerName, time, jailName, cellName, false, "", reason, false, "", jailer, "");
		Player player = Util.getPlayer(playerName, false);	
		
		PrisonerManager.PrepareJail(prisoner, player);
		
		return prisoner;

	}		
	/**
	 * Check if is specified player jailed
	 * @param playerName name of the player you want to check
	 * @return true if player is jailed, false if he is not jailed
	 */
	public Boolean isPlayerJailed(String playerName)
	{
		if (Jail.prisoners.containsKey(playerName.toLowerCase()))
			return true;
		else
			return false;
	}
	
	/**
	 * Get prisoner data for the specific player
	 * @param name name of the player that you want to get data for.
	 * @return JailPrisoner class.
	 */
	public JailPrisoner getPrisoner(String name)
	{
		return Jail.prisoners.get(name);
	}
	
	/**
	 * Get prisoner data of all jailed prisoners on the server
	 * @return
	 */
	public Collection<JailPrisoner> getAllPrisoners()
	{
		return Jail.prisoners.values();
	}
	
	
	/**
	 * Get jail zone data for the specified jail zone
	 * @param name name of the jail zone you want to get data for.
	 * @return
	 */
	public JailZone getJailZone(String name)
	{
		return Jail.zones.get(name);
	}
	
	/**
	 * Get jail zone data for all jail zones on the server.
	 * @return
	 */
	public Collection<JailZone> getAllZones()
	{
		return Jail.zones.values();
	}
	
	/**
	 * Get nearest jail zone to specific location
	 * @param loc Location you want to check against zones
	 * @return nearest jail zone
	 */
	public JailZone getNearestJailZone(Location loc)
	{
		return JailZoneManager.findNearestJail(loc);
	}
	
	/**
	 * Insert new jail zone into jail system. Useful for example when your plugin creates its own jail zone.
	 * @param zone new zone you want to insert into jail system.
	 */
	public void InsertJailZone(JailZone zone)
	{
		InputOutput.InsertZone(zone);
		Jail.zones.put(zone.getName(), zone);
	}
	
	/**
	 * Insert new prisoner into jail system. Use if you want to jail someone manually.
	 * @param prisoner new prisoner that you want to insert into jail system
	 */
	public void InsertPrisoner(JailPrisoner prisoner)
	{
		InputOutput.InsertPrisoner(prisoner);
		Jail.prisoners.put(prisoner.getName(), prisoner);
	}
	
}
