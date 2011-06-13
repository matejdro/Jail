package com.matejdro.bukkit.jail.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.JailZone;
import com.matejdro.bukkit.jail.Util;

public class JailSetCommand extends BaseCommand {
	public static HashMap<String,SelectionPlayer> players = new HashMap<String,SelectionPlayer>();
	public JailSetCommand()
	{
		needPlayer = false;
		permission = "jail.command.jailset";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		List<String> jailcommands = Arrays.asList(new String[]{"telepoint", "releasepoint", "corner1", "corner2"});
		List<String> prisonercommands = Arrays.asList(new String[]{"time", "reason"});
		
		if (args.length > 1 && jailcommands.contains(args[1]))
			JailSetJail(sender, args);
		else if (args.length > 2 && prisonercommands.contains(args[1]))
			JailSetPlayer(sender, args);
		else
		{
			Util.Message("Usage: /jailset [Jail/Player name] [Parameter] (Value)", sender);
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
		}
	}
	
	private static class SelectionPlayer
	{		
		private JailZone jail;
		int change;
		public SelectionPlayer(JailZone zone, int Change)
		{
			jail = zone;
			change = Change;
		}
	}

}
