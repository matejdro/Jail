package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.Settings;
import com.matejdro.bukkit.jail.Util;

public class JailPayCommand extends BaseCommand {
	
	public JailPayCommand()
	{
		needPlayer = true;
		permission = "jail.command.jailpay";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		if (!Settings.EnablePaying) return false;
		if (!(sender instanceof Player) || !Util.permission((Player) sender, "jail.command.jailpay", ((Player) sender).isOp())) return false; 
		if (args.length < 1)
		{
			JailPrisoner prisoner = Jail.prisoners.get(((Player) sender).getName().toLowerCase());
			if (prisoner == null) 
			{
				Util.Message("You are not jailed!", sender);
				return true;
			}
			if (Settings.PriceForInfiniteJail > 0  && prisoner.getRemainingTime() < 0)
				Util.Message("To get out of this mess, you will have to pay " + Settings.PriceForInfiniteJail + " " + Settings.iConomyMoneyName +".", sender);
			else if (prisoner.getRemainingTime() < 0 || Settings.PricePerMinute == 1)
				Util.Message("Sorry, money won't help you this time.", sender);
			else
			{
				String message = "1 minute of your sentence will cost you " + Settings.PricePerMinute + " " + Settings.iConomyMoneyName +". ";
				message += "That means that cost for releasing you out of the jail is " + Settings.PricePerMinute * Math.round(prisoner.getRemainingTimeMinutes()) + " " + Settings.iConomyMoneyName +".";
				Util.Message(message, sender);
			}
		}
		else
		{
			JailPrisoner prisoner;
			if (args.length > 1)
			{
				prisoner = Jail.prisoners.get(args[1].toLowerCase());
				
				if (prisoner == null)
				{
					Util.Message("This player is not jailed!", sender);
					return true;
				}	
			}
			else
			{
				prisoner = Jail.prisoners.get(((Player) sender).getName().toLowerCase());
				if (prisoner == null) 
				{
					Util.Message("You are not jailed!", sender);
					return true;
				}
			}
			
			if ((prisoner.getRemainingTime() < 0 && Settings.PriceForInfiniteJail == 0) || (prisoner.getRemainingTime() > 0 && Settings.PricePerMinute == 0))
			{
				if (args.length > 1)
					Util.Message("Sorry, money won't help him this time.", sender);
				else
					Util.Message("Sorry, money won't help you this time.", sender);
				return true;
			}
			
//			iConomy iConomy = null;
//			Plugin iCoplugin = getServer().getPluginManager().getPlugin("iConomy");
//			
//			if (iCoplugin == null)
//			{
//				Util.Message("iConomy error! Please contact the administrator of your server.", sender);
//				return true;
//			}
//			
//			iConomy = (iConomy) iCoplugin;

		}
		return true;
	}

}
