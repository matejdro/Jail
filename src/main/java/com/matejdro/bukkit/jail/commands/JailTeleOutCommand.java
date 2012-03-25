package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailZone;
import com.matejdro.bukkit.jail.Util;

public class JailTeleOutCommand extends BaseCommand {	
	public JailTeleOutCommand()
	{
		needPlayer = true;
		adminCommand = true;
		permission = "jail.command.jailteleout";
	}
	
	public Boolean run(CommandSender sender, String[] args) {		
		if (args.length < 1)
			Util.Message("Usage: /jailteleout [Name]",sender);
		else if (!Jail.zones.containsKey(args[0].toLowerCase()))
		{
			Util.Message("There is no such jail!", sender);
		}
		else
		{
			JailZone jail = Jail.zones.get(args[0].toLowerCase());
			((Player) sender).teleport(jail.getReleaseTeleportLocation());
		}
		
		return true;
		
	}

}
