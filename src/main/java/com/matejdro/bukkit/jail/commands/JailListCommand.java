package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailZone;
import com.matejdro.bukkit.jail.Util;

public class JailListCommand extends BaseCommand {
	
	public JailListCommand()
	{
		needPlayer = false;
		adminCommand = true;
		permission = "jail.command.jaillist";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		String message = "Jail list: ";
		if (Jail.zones.size() == 0)
		{
			message+= "You have no jails!";
		}
		else
		{
			for (JailZone z : Jail.zones.values())
			{
			message+= z.getName() + " ";	
			}
		}
		Util.Message(message, sender);
		return true;
	}

}
