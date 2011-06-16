package com.matejdro.bukkit.jail;

import org.bukkit.Location;

public class JailZoneManager {
	
	/**
	 * Find nearest jail zone to specified location.
	 */
	public static JailZone findNearestJail(Location loc)
	{
		JailZone jail = null;
		double len = -1;
		
		for (JailZone i : Jail.zones.values())
		{
			double clen = i.getDistance(loc);
			
			if (clen < len || len == -1)
			{
				len = clen;
				jail = i;
			}
				
		}
		
		return jail;
			
	}
	
	/** 
	 * Find nearest jail zone to specified location with ignoring specified jail
	 * @param ignore name of the jail zone that will be ignored while searching
	 */
	public static JailZone findNearestJail(Location loc, String ignore)
	{
		JailZone jail = null;
		double len = -1;
		
		for (JailZone i : Jail.zones.values())
		{
			if (i.getName().equalsIgnoreCase(ignore)) continue;
			double clen = i.getDistance(loc);
			
			if (clen < len || len == -1)
			{
				len = clen;
				jail = i;
			}
				
		}
		
		return jail;
			
	}
	
	/**
	 * Check if location is inside any jail
	 */
	public static Boolean isInsideJail(Location loc)
	{
		for (JailZone zone : Jail.zones.values())
		{
			if (zone.isInside(loc))
			{
				return true;
			}
		}
		return false;
	}
}
