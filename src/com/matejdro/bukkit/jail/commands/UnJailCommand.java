package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.Util;

public class UnJailCommand extends BaseCommand {
	
	public UnJailCommand()
	{
		needPlayer = false;
		adminCommand = true;
		permission = "jail.command.unjail";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		if (args.length < 1)
		{
			Util.Message("Usage: /unjail [Name]", sender);
			return true;
		}
		if (!Jail.prisoners.containsKey(args[0].toLowerCase()))
		{
			Util.Message("That player is not jailed!", sender);
			return true;
		}
		String playername = args[0].toLowerCase();
		JailPrisoner prisoner = Jail.prisoners.get(playername);
		
		prisoner.release();
		
		if (Jail.instance.getServer().getPlayer(prisoner.getName()) == null)
			Util.Message("Player is offline. He will be automatically released when he connnects.", sender);
		else
			Util.Message("Player released", sender);
		return true;
	}

}
