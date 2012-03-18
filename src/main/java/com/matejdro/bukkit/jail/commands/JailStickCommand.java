package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.jail.InputOutput;
import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.Setting;
import com.matejdro.bukkit.jail.Util;

public class JailStickCommand extends BaseCommand {	
	public JailStickCommand()
	{
		needPlayer = true;
		adminCommand = true;
		permission = "jail.usercmd.jailstick";
	}
	
	public Boolean run(CommandSender sender, String[] args) {		
		
		Player player = (Player) sender;
		Boolean enabled = Jail.jailStickToggle.get(player);
		
		if (enabled == null || !enabled)
		{
			Util.Message(InputOutput.global.getString(Setting.MessageJailStickEnabled.getString()), sender);
			Jail.jailStickToggle.put(player, true);
		}
		else
		{
			Util.Message(InputOutput.global.getString(Setting.MessageJailStickDisabled.getString()), sender);
			Jail.jailStickToggle.put(player, false);
		}
		
		return true;
	}

}
