package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.Util;

public class JailCheckCommand extends BaseCommand {
	
	public JailCheckCommand()
	{
		needPlayer = false;
		adminCommand = true;
		permission = "jail.command.jailcheck";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		if (args.length < 1)
		{
		
			String message = "Jailed players: ";
			if (Jail.prisoners.size() == 0)
			{
				message+= "Nobody is jailed!";
			}
			else
			{
				for (JailPrisoner p : Jail.prisoners.values())
				{
					String time;
					if (p.getRemainingTime() >= 0)
					{
						double timed = p.getRemainingTimeMinutes();
						String tim;
						if (timed >= 1.0 || timed < 0.0)
							tim = String.valueOf((int) Math.round( timed ) * 1);
						else
							tim = String.valueOf(Math.round( timed * 10.0d ) / 10.0d);
						
						time = tim + "min";
					}
					else
					{
						time = "forever";
					}
				message+= p.getName() + "(" + time + ") ";	
				}
			}
			Util.Message(message, sender);
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

}
