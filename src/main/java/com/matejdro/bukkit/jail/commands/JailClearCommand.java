package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;

public class JailClearCommand extends BaseCommand {
	
	public JailClearCommand()
	{
		needPlayer = false;
		adminCommand = true;
		permission = "jail.command.jailclear";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		Object[] names = Jail.prisoners.keySet().toArray();
		for (Object p : names)
		{
			JailPrisoner prisoner = Jail.prisoners.get((String) p);
			prisoner.release();
		}
		return true;
	}

}
