package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.Setting;
import com.matejdro.bukkit.jail.Settings;
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
			
			Jail.log.info("Player " + playername + " was released by " + jailer);
		}
		
		return true;
	}

}
