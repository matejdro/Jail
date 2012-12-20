package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.Setting;
import com.matejdro.bukkit.jail.Settings;
import com.matejdro.bukkit.jail.Util;

public class JailStatusCommand extends BaseCommand {
	
	public JailStatusCommand()
	{
		needPlayer = true;
		adminCommand = false;
		permission = "jail.usercmd.jailstatus";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		Player player = (Player) sender;
		JailPrisoner prisoner = Jail.prisoners.get(player.getName().toLowerCase());
		if (!Jail.prisoners.containsKey(player.getName().toLowerCase()))
		{
			Util.Message(Settings.getGlobalString(Setting.MessageYouNotJailed), sender);
			return true;
		}
		
		String message;
		if (prisoner.getReason() == null || prisoner.getReason().trim().equals(""))
			message = prisoner.getJail().getSettings().getString(Setting.MessageJailStatus);
		else
			message = prisoner.getJail().getSettings().getString(Setting.MessageJailStatusReason);
		
		message = prisoner.parseTags(message);
		Util.Message(message, player);
		return true;
	}

}
