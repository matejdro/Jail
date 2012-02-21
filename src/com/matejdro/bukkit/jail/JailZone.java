package com.matejdro.bukkit.jail;

import java.util.HashSet;

import org.bukkit.Location;

public class JailZone {
	String name;
	private double X1;
	private double Y1;
	private double Z1;
	private double X2;
	private double Y2;
	private double Z2;
	private double teleX;
	private double teleY;
	private double teleZ;
	private double freeX;
	private double freeY;
	private double freeZ;
	private String teleWorldname;
	private String freeWorldname;
	private HashSet<JailCell> cells = new HashSet<JailCell>();
	private Settings settings;
	
	/**
	 * @param pname name of new jail zone
	 * @param pX1 X coordinate of the first corner of cuboid of this jail zone 
	 * @param pY1 Y coordinate of the first corner of cuboid of this jail zone
	 * @param pZ1 Z coordinate of the first corner of cuboid of this jail zone
	 * @param pX2 X coordinate of the second corner of cuboid of this jail zone
	 * @param pY2 Y coordinate of the second corner of cuboid of this jail zone
	 * @param pZ2 Z coordinate of the second corner of cuboid of this jail zone
	 * @param pteleX X coordinate of the location, where players get teleported after being jailed in this jail zone 
	 * @param pteleY Y coordinate of the location, where players get teleported after being jailed in this jail zone
	 * @param pteleZ Z coordinate of the location, where players get teleported after being jailed in this jail zone
	 * @param pfreeX X coordinate of the location, where players get teleported after being released from this jail zone
	 * @param pfreeY Y coordinate of the location, where players get teleported after being released from this jail zone
	 * @param pfreeZ Z coordinate of the location, where players get teleported after being released from this jail zone
	 * @param pteleWorld name of the world, which contains the location, where players get teleported after being jailed in this jail zone
	 * @param pfreeWorld name of the world, which contains the location, where players get teleported after being released from this jail zone
	 */
	public JailZone(String pname, double pX1, double pY1, double pZ1, double pX2, double pY2, double pZ2, double pteleX, double pteleY, double pteleZ, double pfreeX, double pfreeY, double pfreeZ, String pteleWorld, String pfreeWorld)
	{
		name = pname.toLowerCase();
		X1 = pX1;
		Y1 = pY1;
		Z1 = pZ1;
		X2 = pX2;
		Y2 = pY2;
		Z2 = pZ2;
		teleX = pteleX;
		teleY = pteleY;
		teleZ = pteleZ;
		freeX = pfreeX;
		freeY = pfreeY;
		freeZ = pfreeZ;
		teleWorldname = pteleWorld;
		freeWorldname = pfreeWorld;
		settings = new Settings(this);
	}
	
	/**
	 * @return name of the jail zone
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * @return Location, where players get teleported after being jailed in this jail zone. Overriden if player is jailed into cell.
	 */
	public Location getTeleportLocation()
	{
		
		return (new Location(Jail.instance.getServer().getWorld(teleWorldname), teleX, teleY, teleZ));
	}
	
	/**
	 * Change location, where players get teleported after being jailed in this jail zone
	 * 
	 * @param input New teleport location
	 * 
	 */
	public void setTeleportLocation(Location input)
	{
		teleX = input.getX();
		teleY = input.getY();
		teleZ = input.getZ();
		teleWorldname = input.getWorld().getName();
	}
	
	/**
	 * @return location, where players get teleported after being released in this jail zone
	 */
	public Location getReleaseTeleportLocation()
	{
		return (new Location(Jail.instance.getServer().getWorld(freeWorldname), freeX, freeY, freeZ));
	}
	
	/**
	 * Change location, where players get teleported after being released in this jail zone
	 * 
	 * @param input New teleport location
	 */
	public void setReleaseTeleportLocation(Location input)
	{
		freeX = input.getX();
		freeY = input.getY();
		freeZ = input.getZ();
		freeWorldname = input.getWorld().getName();
	}
	
	/**
	 * @return Location of the first corner of cuboid of this jail zone
	 */
	public Location getFirstCorner()
	{
		return (new Location(Jail.instance.getServer().getWorld(teleWorldname), X1, Y1, Z1));
	}
	
	/**
	 * Change first corner of cuboid of this jail zone
	 * 
	 * @param input Location of the new corner
	 */
	public void setFirstCorner(Location input)
	{
		X1 = input.getX();
		Y1 = input.getY();
		Z1 = input.getZ();
	}
	
	/**
	 * @return Location of the second corner of cuboid of this jail zone
	 */
	public Location getSecondCorner()
	{
		return (new Location(Jail.instance.getServer().getWorld(teleWorldname), X2, Y2, Z2));
	}
	
	/**
	 * Change second corner of cuboid of this jail zone
	 * 
	 * @param input Location of the new corner
	 */
	public void setSecondCorner(Location input)
	{
		X2 = input.getX();
		Y2 = input.getY();
		Z2 = input.getZ();
	}

	
	/**
	 * @return First empty cell in this jail zone.
	 */
	public JailCell getEmptyCell()
	{
		for (JailCell c : cells)
		{
			if (c.getName() != null && getSettings().getList(Setting.ManualCells).contains(c.getName())) continue;
			if (c.getPlayerName() == null || c.getPlayerName().trim().equals("") || !Jail.prisoners.containsKey(c.getPlayerName().toLowerCase()))
			{
				return c;
			}
		}
		return null;
	}
	
	/**
	 * @param prisoner
	 * @return cell that jailer chosen for this prisoner. Will be null if there is no such cell.
	 */
	public JailCell getRequestedCell(JailPrisoner prisoner)
	{
		for (JailCell cell : getCellList())
		{
			if (cell.getName() != null && cell.getName().equals(prisoner.getRequestedCell())) return cell;
			if (cell.getPlayerName() != null && cell.getPlayerName().equals(prisoner.getName())) return cell;
		}
		return null;
	}
	
	/**
	 * @return nearest cell to the specified location. Measured using teleport location.
	 */
	public JailCell getNearestCell(Location loc)
	{
		JailCell cell = null;
		double distance = -1;
	
		for (JailCell c : getCellList())
		{				
			double dist = c.getTeleportLocation().distance(loc);
			if (dist < distance || distance < 0)
			{
				cell = c;
				distance = dist;
			}
		}
		
		return cell;
	}
	
	/**
	 * @return cell with specified name
	 */
	public JailCell getCell(String name)
	{
		JailCell cell = null;
		for (JailCell c : getCellList())
		{
			if (c.getName() != null && c.getName().equals(name)) 
			{
				cell = c;
				break;
			}
		}
		return cell;
	}
	
	/**
	 * Checks if location is inside this jail zone
	 * 
	 * @param loc Location to check
	 * @return true, if location is inside this jail zone. Otherwise, false.
	 */
	public Boolean isInside(Location loc)
	{
		if (loc.getWorld().getName().equalsIgnoreCase(teleWorldname) && isBetween(X1,X2, loc.getX()) && isBetween(Y1,Y2, loc.getY()) && isBetween(Z1,Z2, loc.getZ()) )
			return true;
		return false;
	}
	
	/**
	 * @param loc Location to check
	 * @return squared distance between teleport location of this jail zone and specified location in blocks. If locations are not in same world, distance cannot be calculated and it will return 2147483647. 
	 */
	public double getDistance(Location loc)
	{
		if (loc.getWorld() != getTeleportLocation().getWorld()) return (double) Integer.MAX_VALUE;
		return loc.distance(getTeleportLocation());
	}
	
	/**
	 * @return list of cells in this jail zone
	 */
	public HashSet<JailCell> getCellList()
	{
		return cells;
	}
	
	/**
	 * @return all prisoners that are jailed inside this jail zone
	 */
	public HashSet<JailPrisoner> getPrisoners()
	{
		HashSet<JailPrisoner> jailprisoners = new HashSet<JailPrisoner>();
		for (JailPrisoner i : Jail.prisoners.values())
		{
			if (i.getJail() == this)
			{
				jailprisoners.add(i);
			}
		}
		return jailprisoners;

	}
	
	public Settings getSettings()
	{
		return settings;
	}
	
	/**
	 * Update properties of this jail zone into database. 
	 * Remember to do that after doing your changes, 
	 * or changes will revert after server restart.
	 */
	public void update()
	{
		InputOutput.UpdateZone(this);
	}
	
	/**
	 * Delete this jail zone. Use with caution.
	 */
	public void delete()
	{
		PrisonerManager.PrepareTransferAll(this);
		InputOutput.DeleteZone(this);
		Jail.zones.remove(getName());
		for (Object cello : getCellList().toArray())
		{
			JailCell cell = (JailCell) cello;
			cell.delete();
		}
			
	}
	
	private Boolean isBetween(double x, double y, double n)
	{
		if ((x < y) && x <= n && y >= n )
			return true;
		else if ((x > y) && x >= n && y <= n )
			return true;
		return false;
		
			
	}
	
	
}
