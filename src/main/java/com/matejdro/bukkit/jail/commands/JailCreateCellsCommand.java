package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailCellCreation;
import com.matejdro.bukkit.jail.Util;

public class JailCreateCellsCommand extends BaseCommand {
	
	public JailCreateCellsCommand()
	{
		needPlayer = true;
		adminCommand = true;
		permission = "jail.command.jailcreatecells";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		if (args.length < 1)
			Util.Message("Usage: /jailcreatecells [Jail name]",sender);
		else if (!Jail.zones.containsKey(args[0]))
			Util.Message("There is no such jail!", sender);
		else
			JailCellCreation.selectstart((Player) sender, args[0].toLowerCase());
		return true;
		
	}

}
