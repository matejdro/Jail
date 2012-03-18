package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.jail.InputOutput;
import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.Setting;
import com.matejdro.bukkit.jail.Util;

public class JailTransferCommand extends BaseCommand {
	
	public JailTransferCommand()
	{
		needPlayer = false;
		adminCommand = true;
		permission = "jail.command.jailtransfer";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		if (args.length < 1)
		{
			Util.Message("Usage: /jailtransfer [Player Name] (New Jail Name:New Cell Name)", sender);
			return true;
		}
		if (!Jail.prisoners.containsKey(args[0].toLowerCase()))
		{
			Util.Message("That player is not jailed!", sender);
			return true;
		}
		
		String jailname = args[1].toLowerCase();
		if (jailname.contains(":")) jailname = jailname.split(":")[0];		
		
		if (args.length > 1 && !Jail.zones.containsKey(jailname))
		{
			Util.Message("Target jail does not exist!", sender);
			return true;
		}
		
		String playername = args[0].toLowerCase();
		String newjail;
		if (args.length < 2 || args[1].equals(InputOutput.global.getString(Setting.NearestJailCode.getString()))) 
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
		
		//Log transfer into console
		if (InputOutput.global.getBoolean(Setting.LogJailingIntoConsole.getString(), false))
		{
			String jailer;
			if (sender instanceof Player)
				jailer = ((Player) sender).getName();
			else if (sender == null)
				jailer = "other plugin";
			else
				jailer = "console";
			
			Jail.log.info("Player " + playername + " was transferred by " + jailer);
		}


			return true;

	}

}
