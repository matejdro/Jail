package com.matejdro.bukkit.jail.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailCell;
import com.matejdro.bukkit.jail.JailZone;
import com.matejdro.bukkit.jail.Util;

public class JailListCellsCommand extends BaseCommand {
	
	public JailListCellsCommand()
	{
		needPlayer = false;
		adminCommand = true;
		permission = "jail.command.jaillistcells";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		if (args.length < 1)
			Util.Message("Usage: /jaillistcells [Jail name]",sender);
		else if (!Jail.zones.containsKey(args[0].toLowerCase()))
		{
			Util.Message("There is no such jail!", sender);
			return true;
		}
		else
		{
			List<String> cellnames = new ArrayList<String>();
			JailZone jail = Jail.zones.get(args[0].toLowerCase());
			for (JailCell cell : jail.getCellList())
			{
				if (cell.getName() != null && !cell.getName().trim().equals(""))
					cellnames.add(cell.getName());
			}
			String message = "Cell list: ";
			if (Jail.zones.size() == 0)
			{
				message+= "You have no named cells in this jail!";
			}
			else
			{
				for (String s : cellnames)
				{
				message+= s + " ";	
				}
			}
			Util.Message(message, sender);
			
			if (sender instanceof Player)
			{
				JailCell cell = jail.getNearestCell(((Player) sender).getLocation());
				if (cell != null && cell.getName() != null && !cell.getName().trim().equals(""))
				{
					Util.Message("Nearest cell: " + cell.getName(), sender);
				}
				
			}
			return true;
			
			
		}
		return true;
	}

}
