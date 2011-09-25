package com.matejdro.bukkit.jail.commands;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailZoneCreation;
import com.matejdro.bukkit.jail.Util;

public class JailCreateWeCommand extends BaseCommand {	
	public JailCreateWeCommand()
	{
		needPlayer = true;
		adminCommand = true;
		permission = "jail.command.jailcreatewe";
	}
	
	public Boolean run(CommandSender sender, String[] args) {
		if (args.length < 1)
		{
			Util.Message("Usage: /jailcreatewe [Name]",sender);
			return true;
		}	
		else if (Jail.zones.containsKey(args[0].toLowerCase()))
		{
			Util.Message("Jail with that name already exist!", sender);
			return true;
		}
			
		Block[] corners = Util.getWorldEditRegion((Player) sender);
		if (corners == null) return true;
		
		JailZoneCreation.selectstart((Player) sender, args[0].toLowerCase());
		JailZoneCreation.select((Player) sender, corners[0]);
		JailZoneCreation.select((Player) sender, corners[1]);
		return true;
		
	}

}
