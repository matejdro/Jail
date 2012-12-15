package com.matejdro.bukkit.jail.commands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.jail.InputOutput;
import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.Setting;
import com.matejdro.bukkit.jail.Settings;
import com.matejdro.bukkit.jail.Util;

public class JailCheckCommand extends BaseCommand {
	
	public JailCheckCommand()
	{
		needPlayer = false;
		adminCommand = true;
		permission = "jail.command.jailcheck";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		if (args.length < 1 || Util.isInteger(args[0]))
		{
		
			String message = "Jailed players: ";
			if (Jail.prisoners.size() == 0)
			{
				message+= "Nobody is jailed!";
				Util.Message(message, sender);
			}
			else
			{
				listAllPrisoners(sender, args);
			}
		}
		else
		{

			String name = args[0].toLowerCase();
			JailPrisoner prisoner = Jail.prisoners.get(name);
			String message ="";
			if (!Jail.prisoners.containsKey(name))
			{
				Util.Message("§aPlayer is not jailed!", sender);
				return true; 
			}
			else if (prisoner.getRemainingTime() < 0)
			{
				message += ("Player is jailed forever! (or until admin releases him)");
			}

			else if (prisoner.getRemainingTime() != 0)
			{
				double time = prisoner.getRemainingTimeMinutes();
				String tim;
				if (time >= 1.0 || time < 0.0)
					tim = String.valueOf((int) Math.round( time ) * 1);
				else
					tim = String.valueOf(Math.round( time * 10.0d ) / 10.0d);
				
				message += ("Player is jailed for " + tim + " minutes");
			}
			
			if (prisoner.getReason() != null && !prisoner.getReason().trim().equals(""))
			{
				message += " because " + prisoner.getReason();
			}
			
			message += " by " + prisoner.getJailer();
			Util.Message(message, sender);
		}
		return true;
	}
	
	private void listAllPrisoners(CommandSender sender, String[] args)
	{
		String[] allPrisoners = Jail.prisoners.keySet().toArray(new String[0]);
		Arrays.sort(allPrisoners);
		
		for (String name : allPrisoners)
		{
			JailPrisoner prisoner = Jail.prisoners.get(name);
			if (prisoner.getJail() == null)
				Util.Message(Settings.getGlobalString(Setting.MessageJailCheckLineWaitingOffline).replace("<Player>", name), sender);
			else
				Util.Message(prisoner.parseTags(Settings.getGlobalString(Setting.MessageJailCheckLine)), sender);
				
		}
			
	}

}
