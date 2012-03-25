package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.Util;

public class UnJailForceCommand extends BaseCommand {
	
	public UnJailForceCommand()
	{
		needPlayer = false;
		adminCommand = true;
		permission = "jail.command.unjailforce";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		if (args.length < 1)
		{
			Util.Message("Usage: /unjailforce [Name]", sender);
			return true;
		}
		if (!Jail.prisoners.containsKey(args[0].toLowerCase()))
		{
			Util.Message("That player is not jailed!", sender);
			return true;
		}
		String playername = args[0].toLowerCase();
		Jail.prisoners.get(playername).delete();
		Util.Message("Player deleted from the jail database!", sender);
		return true;

	}

}
