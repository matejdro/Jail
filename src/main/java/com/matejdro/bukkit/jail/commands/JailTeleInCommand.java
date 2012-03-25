package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailZone;
import com.matejdro.bukkit.jail.Util;

public class JailTeleInCommand extends BaseCommand {	
	public JailTeleInCommand()
	{
		needPlayer = true;
		adminCommand = true;
		permission = "jail.command.jailtelein";
	}
	
	public Boolean run(CommandSender sender, String[] args) {		
		if (args.length < 1)
			Util.Message("Usage: /jailtelein [Name]",sender);
		else if (!Jail.zones.containsKey(args[0].toLowerCase()))
		{
			Util.Message("There is no such jail!", sender);
		}
		else
		{
			JailZone jail = Jail.zones.get(args[0].toLowerCase());
			((Player) sender).teleport(jail.getTeleportLocation());
		}
		return true;
		
	}

}
