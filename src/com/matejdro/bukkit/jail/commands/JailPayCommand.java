package com.matejdro.bukkit.jail.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.iConomy.iConomy;
import com.iConomy.system.Account;
import com.iConomy.system.Holdings;
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
		
		Player player = (Player) sender;
		
		iConomy iConomy = null;
		Plugin iCoplugin = Jail.instance.getServer().getPluginManager().getPlugin("iConomy");
		
		if (iCoplugin == null)
		{
			Util.Message("iConomy error! Please contact the administrator of your server.", sender);
			return true;
		}
		
		iConomy = (iConomy) iCoplugin;		
		if (args.length < 1)
		{
			JailPrisoner prisoner = Jail.prisoners.get(((Player) sender).getName().toLowerCase());
			if (prisoner == null) 
			{
				Util.Message("You are not jailed!", sender);
				return true;
			}
			if (Settings.PriceForInfiniteJail > 0  && prisoner.getRemainingTime() < 0)
				Util.Message("To get out of this mess, you will have to pay " + iConomy.format(Settings.PriceForInfiniteJail) +".", sender);
			else if (prisoner.getRemainingTime() < 0 || Settings.PriceForInfiniteJail == 0)
				Util.Message("Sorry, money won't help you this time.", sender);
			else
			{
				String message = "1 minute of your sentence will cost you " + iConomy.format(Settings.PricePerMinute) +". ";
				message += "That means that cost for releasing you out of the jail is " + iConomy.format(Settings.PricePerMinute * Math.round(prisoner.getRemainingTimeMinutes())) +".";
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
			
			
			Account account = iConomy.getAccount(player.getName());
			Holdings holdings = account.getHoldings();
			
			double payment = Double.parseDouble(args[0]);
			
			if (holdings.hasUnder(payment))
			{
					Util.Message("You don't have that much money!", sender);
					return true;
			}
			
			if (prisoner.getRemainingTime() < 0)
			{
				if (payment >= Settings.PriceForInfiniteJail)
				{
					if (args.length > 1)
						Util.Message("You have just payed " + iConomy.format(Settings.PriceForInfiniteJail) + " and saved " + prisoner.getName() + " from the jail!", sender);
					else
						Util.Message("You have just payed " + iConomy.format(Settings.PriceForInfiniteJail) + " and saved yourself from the jail!", sender);
					holdings.subtract(Settings.PriceForInfiniteJail);
					prisoner.release();
				}
				else
				{
					Util.Message("That won't be enough!", sender);
					return true;
				}
			}
			else
			{
				double releasebill = Settings.PricePerMinute * Math.round(prisoner.getRemainingTimeMinutes());
				if (payment >= releasebill)
				{
					if (args.length > 1)
						Util.Message("You have just payed " + iConomy.format(releasebill) + " and saved " + prisoner.getName() + " from the jail!", sender);
					else
						Util.Message("You have just payed " + iConomy.format(releasebill) + " and saved yourself from the jail!", sender);
					holdings.subtract(releasebill);
					prisoner.release();
				}
				else
				{
					int minutes = (int) Math.round(payment / Settings.PricePerMinute);
					double bill = minutes * Settings.PricePerMinute;
					int remain = (int) (Math.round(prisoner.getRemainingTimeMinutes()) - minutes);
					if (args.length > 1)
						Util.Message("You have just payed " + iConomy.format(bill) + " and lowered " + prisoner.getName() + "'s sentence to " + String.valueOf(remain) + " minutes!", sender);
					else
						Util.Message("You have just payed " + iConomy.format(bill) + " and lowered your sentence to " + String.valueOf(remain) + " minutes!", sender);
					holdings.subtract(bill);
					prisoner.setRemainingTimeMinutes(remain);
				}
			}

		}
		return true;
	}

}
