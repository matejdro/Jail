package com.matejdro.bukkit.jail;

import org.bukkit.Location;

public class JailZoneManager {
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
