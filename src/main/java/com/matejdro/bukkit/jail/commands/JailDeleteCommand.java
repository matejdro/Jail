package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailZone;
import com.matejdro.bukkit.jail.Setting;
import com.matejdro.bukkit.jail.Settings;
import com.matejdro.bukkit.jail.Util;

public class JailDeleteCommand extends BaseCommand {
	
	public JailDeleteCommand()
	{
		needPlayer = false;
		adminCommand = true;
		permission = "jail.command.jaildelete";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		if (args.length < 1)
			Util.Message("Usage: /jaildelete [Name]",sender);
		else if (!Jail.zones.containsKey(args[0].toLowerCase()))
		{
			Util.Message("There is no such jail!", sender);
			return true;
		}
		else
		{
			if (Jail.zones.size() < 2 && Jail.prisoners.size() > 0)
			{
				Util.Message("You cannot delete last jail zone! Please empty it first (release all players)!",sender);
				return true;
			}
			else
			{

				JailZone zone = Jail.zones.get(args[0].toLowerCase());
				zone.delete();
				Util.Message("Jail deleted", sender);
				
				//Log release into console
				if (Settings.getGlobalBoolean(Setting.LogJailingIntoConsole))
				{
					String jailer;
					if (sender instanceof Player)
						jailer = ((Player) sender).getName();
					else if (sender == null)
						jailer = "other plugin";
					else
						jailer = "console";
					
					Jail.log.info("Jail " + args[0] + " was deleted by " + jailer);
				}
				
				return true;
			}
		}
		

		
		return true;
	}

}
