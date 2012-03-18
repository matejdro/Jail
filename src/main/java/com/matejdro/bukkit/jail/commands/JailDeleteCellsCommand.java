package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailCell;
import com.matejdro.bukkit.jail.JailZone;
import com.matejdro.bukkit.jail.Util;

public class JailDeleteCellsCommand extends BaseCommand {
	
	public JailDeleteCellsCommand()
	{
		needPlayer = false;
		adminCommand = true;
		permission = "jail.command.jaildeletecells";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		if (args.length < 1)
			Util.Message("Usage: /jaildeletecells [Jail name]",sender);
		else if (!Jail.zones.containsKey(args[0].toLowerCase()))
		{
			Util.Message("There is no such jail!", sender);
			return true;
		}
		else
		{
			JailZone jail = Jail.zones.get(args[0].toLowerCase());
			for (Object cello : jail.getCellList().toArray())
			{
				JailCell cell = (JailCell) cello;
				cell.delete();
			}
			
			Util.Message("Cells deleted", sender);
			
			
		}
		return true;
	}

}
