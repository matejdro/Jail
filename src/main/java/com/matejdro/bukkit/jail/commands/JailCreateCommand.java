package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailZoneCreation;
import com.matejdro.bukkit.jail.Util;

public class JailCreateCommand extends BaseCommand {	
	public JailCreateCommand()
	{
		needPlayer = true;
		adminCommand = true;
		permission = "jail.command.jailcreate";
	}
	
	public Boolean run(CommandSender sender, String[] args) {		
		if (args.length < 1)
			Util.Message("Usage: /jailcreate [Name]",sender);
		else if (Jail.zones.containsKey(args[0].toLowerCase()))
			Util.Message("Jail with that name already exist!", sender);
		else
			JailZoneCreation.selectstart((Player) sender, args[0].toLowerCase());
		return true;
		
	}

}
