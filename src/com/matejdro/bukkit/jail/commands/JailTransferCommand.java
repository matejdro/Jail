package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.Settings;
import com.matejdro.bukkit.jail.Util;

public class JailTransferCommand extends BaseCommand {
	
	public JailTransferCommand()
	{
		needPlayer = false;
		permission = "jail.command.jailtransfer";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		if (args.length < 1)
		{
			Util.Message("Usage: /jailtransfer [Player Name] (New Jail Name)", sender);
			return true;
		}
		if (!Jail.prisoners.containsKey(args[0].toLowerCase()))
		{
			Util.Message("That player is not jailed!", sender);
			return true;
		}
		if (args.length > 1 && !Jail.zones.containsKey(args[1].toLowerCase()))
		{
			Util.Message("Target jail does not exist!", sender);
			return true;
		}
		String playername = args[0].toLowerCase();
		String newjail;
		if (args.length < 2 || args[1].equals(Settings.NearestJailCode)) 
			newjail = null;
		else
			newjail = args[1].toLowerCase();
		JailPrisoner prisoner = Jail.prisoners.get(playername);
		prisoner.transfer(newjail);

		if (Jail.instance.getServer().getPlayer(playername) == null)
		{
			Util.Message("Player is offline. He will be automatically transfered when he connnects.", sender);
			
		}
		else
		{
			Util.Message("Player transfered.", sender);
			
		}

			return true;

	}

}
