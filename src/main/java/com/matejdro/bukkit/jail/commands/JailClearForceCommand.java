package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.Util;

public class JailClearForceCommand extends BaseCommand {
	
	public JailClearForceCommand()
	{
		needPlayer = false;
		adminCommand = true;
		permission = "jail.command.jailclearforce";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		Object[] names = Jail.prisoners.keySet().toArray();
		for (Object p : names)
		{
			Jail.prisoners.get(p).delete();
		}
		Util.Message("Everyone have been cleared!", sender);
		return true;
		}

}
