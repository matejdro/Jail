package com.matejdro.bukkit.jail.commands;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailZoneCreation;
import com.matejdro.bukkit.jail.Util;
import com.matejdro.bukkit.jail.WorldEditHandler;

public class JailCreateWeCommand extends BaseCommand {	
	public JailCreateWeCommand()
	{
		needPlayer = true;
		adminCommand = true;
		permission = "jail.command.jailcreatewe";
	}
	
	public Boolean run(CommandSender sender, String[] args) {
		if (args.length < 1)
		{
			Util.Message("Usage: /jailcreatewe [Name]",sender);
			return true;
		}	
		else if (Jail.zones.containsKey(args[0].toLowerCase()))
		{
			Util.Message("Jail with that name already exist!", sender);
			return true;
		}
			
		Plugin plugin = Jail.instance.getServer().getPluginManager().getPlugin("WorldEdit");
		if (plugin == null)
		{
			Util.Message("WorldEdit is not installed!", sender);
			return true;
		}
		Block[] corners = WorldEditHandler.getWorldEditRegion((Player) sender);
		if (corners == null) return true;

		
		JailZoneCreation.selectstart((Player) sender, args[0].toLowerCase());
		JailZoneCreation.select((Player) sender, corners[0]);
		JailZoneCreation.select((Player) sender, corners[1]);
		return true;
		
	}

}
