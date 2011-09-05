package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailCell;
import com.matejdro.bukkit.jail.JailZone;
import com.matejdro.bukkit.jail.Util;

public class JailDeleteCellCommand extends BaseCommand {
	
	public JailDeleteCellCommand()
	{
		needPlayer = true;
		adminCommand = true;
		permission = "jail.command.jaildeletecell";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		if (args.length < 1)
		{
			Util.Message("Usage: /jaildeletecell [Jail name] (Cell name)",sender);
			return true;
		}
		if (!Jail.zones.containsKey(args[0]))
		{
			Util.Message("There is no such jail!", sender);
			return true;
		}
		
		String cellname = null;
		if (args.length > 1) cellname = args[1];
			
		JailZone jail = Jail.zones.get(args[0]);
		JailCell cell = null;
		
		if (cellname != null)
		{
			Boolean exist = false;
			for (JailCell c : jail.getCellList())
			{
				if (c.getName() != null && c.getName().equals(cellname)) 
				{
					cell = c;
					exist = true;
					break;
				}
			}
			
			if (!exist)
			{
				Util.Message("There is no such cell!", sender);
				return true;
			}
		}

		//Find closest cell
		if (cell == null)
		{
			double distance = -1;
			for (JailCell c : jail.getCellList())
			{
				double dist = c.getTeleportLocation().distance(((Player) sender).getLocation());
				if (dist < distance || distance < 0)
				{
					cell = c;
					distance = dist;
				}
			}
		}
		
		if (cell == null)
		{
			Util.Message("Cannot find any cell!", sender);
			return true;
		}
		
		cell.delete();
		Util.Message("Cell deleted.", sender);
		
		return true;
	}

}
