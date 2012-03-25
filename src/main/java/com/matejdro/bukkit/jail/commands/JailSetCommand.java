package com.matejdro.bukkit.jail.commands;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.matejdro.bukkit.jail.InputOutput;
import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailCell;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.JailZone;
import com.matejdro.bukkit.jail.Setting;
import com.matejdro.bukkit.jail.Util;
import com.matejdro.bukkit.jail.WorldEditHandler;

public class JailSetCommand extends BaseCommand {
	public static HashMap<String,SelectionPlayer> players = new HashMap<String,SelectionPlayer>();
	public JailSetCommand()
	{
		needPlayer = false;
		adminCommand = true;
		permission = "jail.command.jailset";
	}

	public Boolean run(CommandSender sender, String[] args) {		
		List<String> jailcommands = Arrays.asList(new String[]{"telepoint", "releasepoint", "corner1", "corner2", "manualjail", "worldedit"});
		List<String> prisonercommands = Arrays.asList(new String[]{"time", "reason"});
		List<String> cellcommands = Arrays.asList(new String[]{"celltele", "chest", "addsign", "removesign", "clearsigns", "manualcell"});

		if (args.length > 1 && jailcommands.contains(args[1]))
			JailSetJail(sender, args);
		else if (args.length > 2 && prisonercommands.contains(args[1]))
			JailSetPlayer(sender, args);
		else if (args.length > 1 && cellcommands.contains(args[1]))
			JailSetCell(sender, args);
		else
		{
			Util.Message("Usage: /jailset [Jail/Player name/Jail Name:Cell Name] [Parameter] (Value)", sender);
			return true;
		}

		return true;
	}
		
	public static void JailSetJail(CommandSender sender, String[] args)
	{
		String parameter = args[1];
		
		if (!(sender instanceof Player))
		{
			Util.Message("You cannot do that via console!", sender);
			return;
		}
		
		JailZone jail = Jail.zones.get(args[0]);
		if (jail == null)
		{
			Util.Message("There is no such jail!", sender);
			return;
		}
		
		if (parameter.equals("telepoint"))
		{
			Util.Message("Move to the desired teleport point and right click any block with wooden sword", sender);
			players.put(((Player) sender).getName(), new SelectionPlayer(jail, 0));
		}
		else if (parameter.equals("releasepoint"))
		{
			Util.Message("Move to the desired release teleport point and right click any block with wooden sword", sender);
			players.put(((Player) sender).getName(), new SelectionPlayer(jail, 1));
		}
		else if (parameter.equals("corner1"))
		{
			Util.Message("Right click on the block with wooden sword to change first corner point to that block", sender);
			players.put(((Player) sender).getName(), new SelectionPlayer(jail, 2));
		}
		else if (parameter.equals("corner2"))
		{
			Util.Message("Right click on the block with wooden sword to change second corner point to that block", sender);
			players.put(((Player) sender).getName(), new SelectionPlayer(jail, 3));
		}
		else if (parameter.equals("manualjail"))
		{
			List<String> list = (List<String>) InputOutput.global.get(Setting.ManualJails.getString());
			
			if (list.contains(jail.getName()))
			{
				list.remove(jail.getName());
				Util.Message("Jail is set to automatic", sender);
			}
			else
			{
				list.add(jail.getName());
				Util.Message("Jail is set to manual", sender);
			}
			
			InputOutput.global.set(Setting.ManualJails.getString(), list);
			try {
				InputOutput.global.save(new File("plugins" + File.separator + "Jail","global.yml"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (parameter.equals("worldedit"))
		{
			Plugin plugin = Jail.instance.getServer().getPluginManager().getPlugin("WorldEdit");
			if (plugin == null)
			{
				Util.Message("WorldEdit is not installed!", sender);
				return;
			}
			Block[] corners = WorldEditHandler.getWorldEditRegion((Player) sender);
			if (corners == null) return;
			
			jail.setFirstCorner(corners[0].getLocation());
			jail.setSecondCorner(corners[1].getLocation());
			jail.update();

			Util.Message("Jail cuboid changed!", sender);
		}
	}
	
	public static void JailSetPlayer(CommandSender sender, String[] args)
	{
		String parameter = args[1];
		JailPrisoner prisoner = Jail.prisoners.get(args[0]);
		
		if (prisoner == null)
		{
			Util.Message("That player is not jailed!", sender);
			return;
		}
		
		if (parameter.equals("time"))
		{
			prisoner.setRemainingTime(Integer.parseInt(args[2]) * 6);
			prisoner.update();
			Util.Message("Prisoner's remaining time changed!", sender);
		}
		else if (parameter.equals("reason"))
		{
			prisoner.setReason(args[2]);
			prisoner.update();
			Util.Message("Prisoner's jailing reason changed", sender);
		}
			
	}
	
	public static void JailSetCell(CommandSender sender, String[] args)
	{
		String parameter = args[1];
		String jailname = args[0].split(":")[0];
		String cellname = null;
		
		if (args[0].split(":").length > 1)
			cellname = args[0].split(":")[1];
		
		if (!(sender instanceof Player))
		{
			Util.Message("You cannot do that via console!", sender);
			return;
		}
		JailZone jail = Jail.zones.get(jailname);
		if (jail == null)
		{
			Util.Message("There is no such jail!", sender);
			return;
		}
		
		JailCell cell;
		if (cellname != null)
		{
			cell = jail.getCell(cellname);
			
			if (cell == null)
			{
				Util.Message("There is no such cell!", sender);
				return;
			}
		}
		else
		{
			cell = jail.getNearestCell(((Player) sender).getLocation());
		}
		
		if (cell == null)
		{
			Util.Message("Cannot find any cell!", sender);
			return;
		}
		
		if (parameter.equals("celltele"))
		{
			Util.Message("Move to the desired cell teleport point and right click any block with wooden sword", sender);
			players.put(((Player) sender).getName(), new SelectionPlayer(cell, 4));
		}
		else if (parameter.equals("chest"))
		{
			Util.Message("Right click on desired chest to select it. Click on any non-chest block to remove chest from cell", sender);
			players.put(((Player) sender).getName(), new SelectionPlayer(cell, 5));
		}
		else if (parameter.equals("addsign"))
		{
			Util.Message("Right click on desired sign to add it to cell.", sender);
			players.put(((Player) sender).getName(), new SelectionPlayer(cell, 6));
		}
		else if (parameter.equals("removesign"))
		{
			Util.Message("Right click on desired sign to remove it from cell.", sender);
			players.put(((Player) sender).getName(), new SelectionPlayer(cell, 7));
		}
		else if (parameter.equals("clearsigns"))
		{
			for (Sign l : cell.getSigns())
				cell.removeSign(l.getBlock().getLocation());
			cell.update();
			Util.Message("Sign list cleared!", sender);

		}
		else if (parameter.equals("manualcell"))
		{
			if (cell.getName() == null || cell.getName().trim().equals(""))
			{
				Util.Message("This feature is only supported for cells with name!", sender);
				return;
			}
			
			List<String> list = (List<String>) jail.getSettings().getList(Setting.ManualCells);
			
			if (list.contains(cell.getName()))
			{
				list.remove(cell.getName());
				Util.Message("Cell is set to automatic", sender);
			}
			else
			{
				list.add(cell.getName());
				Util.Message("Cell is set to manual", sender);
			}
			
			jail.getSettings().setProperty(Setting.ManualCells, list);
		}
		




	}
	
	public static void RightClick(Block b, Player p)
	{
		SelectionPlayer data = players.get(p.getName());
		switch(data.change)
		{
		case 0:
			Util.Message("Teleport point updated.", p);
			data.jail.setTeleportLocation(p.getLocation());
			data.jail.update();
			players.remove(p.getName());
			break;
		case 1:
			Util.Message("Release teleport point updated.", p);
			data.jail.setReleaseTeleportLocation(p.getLocation());
			data.jail.update();
			players.remove(p.getName());
			break;
		case 2:
			Util.Message("First corner updated.", p);
			data.jail.setFirstCorner(b.getLocation());
			data.jail.update();
			players.remove(p.getName());
			break;
		case 3:
			Util.Message("Second corner updated.", p);
			data.jail.setSecondCorner(b.getLocation());
			data.jail.update();
			players.remove(p.getName());
			break;
		case 4:
			Util.Message("Cell teleport point updated.", p);
			data.cell.setTeleportLocation(p.getLocation());
			data.cell.update();
			players.remove(p.getName());
			break;
		case 5:
			Util.Message("Cell chest updated.", p);
			data.cell.setChest((Location) null);
			data.cell.setSecondChest((Location) null);
			if (b.getType() == Material.CHEST)
			{
				data.cell.setChest(b.getLocation());
				if (b.getRelative(BlockFace.NORTH).getType() == Material.CHEST) data.cell.setSecondChest(b.getRelative(BlockFace.NORTH).getLocation());
				else if (b.getRelative(BlockFace.SOUTH).getType() == Material.CHEST) data.cell.setSecondChest(b.getRelative(BlockFace.SOUTH).getLocation());
				else if (b.getRelative(BlockFace.EAST).getType() == Material.CHEST) data.cell.setSecondChest(b.getRelative(BlockFace.EAST).getLocation());
				else if (b.getRelative(BlockFace.WEST).getType() == Material.CHEST) data.cell.setSecondChest(b.getRelative(BlockFace.WEST).getLocation());

			}
			data.cell.update();
			players.remove(p.getName());
			break;
		case 6:
			if (b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN)
			{
				data.cell.addSign(b.getLocation());
				data.cell.update();
				Util.Message("Sign added.", p);
			}
			else
			{
				Util.Message("You have not selected sign. Selection cancelled.", p);
			}

			players.remove(p.getName());
			break;
		case 7:
			if (b.getState() instanceof Sign && data.cell.getSigns().contains(b.getState()))
			{
				data.cell.removeSign(b.getLocation());
				data.cell.update();
				Util.Message("Sign removed!", p);
			}
			else
			{
				Util.Message("This sign is not associated with your cell! Selection cancelled.", p);
			}
			players.remove(p.getName());
			break;
			

		}
	}
	
	private static class SelectionPlayer
	{		
		private JailZone jail;
		private JailCell cell;
		int change;
		public SelectionPlayer(JailZone zone, int Change)
		{
			jail = zone;
			change = Change;
		}
		
		public SelectionPlayer(JailCell Cell, int Change)
		{
			cell = Cell;
			change = Change;
		}
	}

}
