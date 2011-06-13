package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.Util;

public class JailStatusCommand extends BaseCommand {
	
	public JailStatusCommand()
	{
		needPlayer = true;
		permission = "jail.command.jailstatus";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		Player player = (Player) sender;
		String message = "";
		JailPrisoner prisoner = Jail.prisoners.get(player.getName().toLowerCase());
		if (!Jail.prisoners.containsKey(player.getName().toLowerCase()))
		{
			Util.Message("§aYou are not jailed!", player);
			return true;
		}
		else if (prisoner.getRemainingTime() < 0)
		{
			message += ("§cYou are jailed forever! (or until admin releases you)");
		}
		else if (prisoner.getRemainingTime() != 0)
		{
			double time = prisoner.getRemainingTimeMinutes();
			String tim;
			if (time >= 1.0 || time < 0.0)
				tim = String.valueOf((int) Math.round( time ) * 1);
			else
				tim = String.valueOf(Math.round( time * 10.0d ) / 10.0d);
			
			message += ("§cYou are jailed for " + tim + " minutes");
		}
		
		if (prisoner.getReason() != null && !prisoner.getReason().trim().equals(""))
		{
			message += " because " + prisoner.getReason();
		}
		
		message += " by " + prisoner.getJailer();
		Util.Message(message, player);
		return true;
	}

}
