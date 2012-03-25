package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.jail.JailCellCreation;
import com.matejdro.bukkit.jail.JailZoneCreation;
import com.matejdro.bukkit.jail.Util;

public class JailStopCommand extends BaseCommand {
	
	public JailStopCommand()
	{
		needPlayer = true;
		adminCommand = true;
		permission = "jail.command.jailstop";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		Player player = (Player) sender;
		JailZoneCreation.players.remove(player.getName());
		JailCellCreation.players.remove(player.getName());
		JailSetCommand.players.remove(player.getName());
		
		Util.Message("Any creation stopped", player);
		return true;
		
	}

}
