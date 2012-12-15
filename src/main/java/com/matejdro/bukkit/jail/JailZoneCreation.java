package com.matejdro.bukkit.jail;

import java.util.HashMap;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class JailZoneCreation {
public static HashMap<String,CreationPlayer> players = new HashMap<String,CreationPlayer>();

	
	public static void selectstart(Player player, String name)
	{
		if (players.containsKey(player.getName()))
		{
			players.remove(player.getName());
		}
		if (!player.getInventory().contains(Settings.getGlobalInt(Setting.SelectionTool)))
			player.getInventory().addItem(new ItemStack(Settings.getGlobalInt(Setting.SelectionTool),1));
		
		Util.Message("§cJail Zone Creation:", player);
		Util.Message("First, you must select jail cuboid. Select first point of the cuboid by right clicking on the block with your wooden sword. DO NOT FORGET TO MARK FLOOR AND CEILING TOO!", player);
		players.put(player.getName(), new CreationPlayer());
		players.get(player.getName()).name = name.toLowerCase();
	}
	
	public static void select(Player player, Block block)
	{
		switch (players.get(player.getName()).state)
		{
			case 1:
				firstpoint(player,block);
				break;
			case 2:
				secondpoint(player,block);
				break;
			case 3:
				telepoint(player);
				break;
			case 4:
				freepoint(player);
				break;
			
			
		}
	}
	
	private static void firstpoint(Player player, Block block)
	{
		Util.Message("First point selected. Now select second point.", player);
		CreationPlayer cr = players.get(player.getName());
		cr.X1 = block.getX();
		cr.Y1 = block.getY();
		cr.Z1 = block.getZ();
		cr.state++;
		
	}

	private static void secondpoint(Player player, Block block)
	{
		Util.Message("Second point selected. Now go inside jail and right click anywhere to select your current position as teleport location.", player);
		CreationPlayer cr = players.get(player.getName());
		cr.X2 = block.getX();
		cr.Y2 = block.getY();
		cr.Z2 = block.getZ();
		cr.state++;
		
	}
	
	
	private static void telepoint(Player player)
	{
		Util.Message("Teleport point selected. Now go outside of jail and right click anywhere to select your current position as location, where people will be teleported after they are released from this jail.", player);
		CreationPlayer cr = players.get(player.getName());
		cr.teleX = player.getLocation().getX();
		cr.teleY = player.getLocation().getY();
		cr.teleZ = player.getLocation().getZ();
		cr.teleWorld = player.getWorld().getName();
		cr.state++;
		
	}
	
	private static void freepoint(Player player)
	{
		
		CreationPlayer cr = players.get(player.getName());
		cr.freeX = player.getLocation().getX();
		cr.freeY = player.getLocation().getY();
		cr.freeZ = player.getLocation().getZ();
		cr.freeWorld = player.getWorld().getName();
		cr.state++;
		
		JailZone z = new JailZone(cr.name, cr.X1, cr.Y1, cr.Z1, cr.X2, cr.Y2, cr.Z2, cr.teleX, cr.teleY, cr.teleZ, cr.freeX, cr.freeY, cr.freeZ, cr.teleWorld, cr.freeWorld);
		
		InputOutput.InsertZone(z);
		
		Jail.zones.put(z.getName(),z);
		
		Util.Message("Jail created successfully!", player);
		
		players.remove(player.getName());
	}

	
	private static class CreationPlayer
	{
		public int state;
		
		private String name;
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
		private String teleWorld;
		private String freeWorld;
		
		public CreationPlayer()
		{
			state = 1;
		}
}

}
