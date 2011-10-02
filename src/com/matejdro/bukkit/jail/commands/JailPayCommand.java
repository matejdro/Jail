package com.matejdro.bukkit.jail.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.matejdro.bukkit.jail.InputOutput;
import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.JailZone;
import com.matejdro.bukkit.jail.Setting;
import com.matejdro.bukkit.jail.Util;
import com.nijikokun.register.Register;
import com.nijikokun.register.payment.Methods;

public class JailPayCommand extends BaseCommand {
	
	public JailPayCommand()
	{
		needPlayer = true;
		adminCommand = false;
		permission = "jail.usercmd.jailpay";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		Player player = (Player) sender;
		
		JailPrisoner prisoner = Jail.prisoners.get(((Player) sender).getName().toLowerCase());
		if (prisoner == null || !prisoner.getJail()..getSettings().getBoolean(Setting.EnablePaying)) 
		{
			return false;
		}
		
		if (args.length < 1)
		{
			if (prisoner == null) 
			{
				Util.Message(InputOutput.global.getString(Setting.MessageYouNotJailed.getString()), sender);
				return true;
			}
			JailZone jail = prisoner.getJail();
			if (jail.getSettings().getDouble(Setting.PriceForInfiniteJail) > 0  && prisoner.getRemainingTime() < 0)
				Util.Message(jail.getSettings().getString(Setting.MessageJailPayAmountForever).replace("<Amount>", format(jail.getSettings().getDouble(Setting.PriceForInfiniteJail), prisoner)), sender);
			else if (prisoner.getRemainingTime() < 0 || jail.getSettings().getDouble(Setting.PriceForInfiniteJail) == 0)
				Util.Message(jail.getSettings().getString(Setting.MessageJailPayCannotPay), sender);
			else
			{
				String message = jail.getSettings().getString(Setting.MessageJailPayCost);
				message = message.replace("<MinutePrice>", format(jail.getSettings().getDouble(Setting.PricePerMinute), prisoner));
				message = message.replace("<WholePrice>", format(jail.getSettings().getDouble(Setting.PricePerMinute) * Math.floor(prisoner.getRemainingTimeMinutes()), prisoner));
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
					Util.Message(InputOutput.global.getString(Setting.MessagePlayerNotJailed.getString()), sender);
					return true;
				}	
			}
			else
			{
				prisoner = Jail.prisoners.get(((Player) sender).getName().toLowerCase());
				if (prisoner == null) 
				{
					Util.Message(InputOutput.global.getString(Setting.MessageYouNotJailed.getString()), sender);
					return true;
				}
			}
			JailZone jail = prisoner.getJail();
			if ((prisoner.getRemainingTime() < 0 && jail.getSettings().getDouble(Setting.PriceForInfiniteJail) == 0) || (prisoner.getRemainingTime() > 0 && jail.getSettings().getDouble(Setting.PricePerMinute) == 0))
			{
				if (args.length > 1)
					Util.Message(jail.getSettings().getString(Setting.MessageJailPayCannotPayHim), sender);
				else
					Util.Message(jail.getSettings().getString(Setting.MessageJailPayCannotPay), sender);
				return true;
			}
						
			double payment = Double.parseDouble(args[0]);
			
			if (!hasEnough(payment, prisoner, player))
			{
					Util.Message(jail.getSettings().getString(Setting.MessageJailPayNotEnoughMoney) , sender);
					return true;
			}
			
			if (prisoner.getRemainingTime() < 0)
			{
				if (payment >= jail.getSettings().getDouble(Setting.PriceForInfiniteJail))
				{
					if (args.length > 1)
						Util.Message(jail.getSettings().getString(Setting.MessageJailPayPaidReleasedHim).replace("<Amount>", format(jail.getSettings().getDouble(Setting.PriceForInfiniteJail), prisoner)).replace("<Prisoner>", prisoner.getName()), sender);
					else
						Util.Message(jail.getSettings().getString(Setting.MessageJailPayPaidReleased).replace("<Amount>", format(jail.getSettings().getDouble(Setting.PriceForInfiniteJail), prisoner)), sender);
					pay(jail.getSettings().getDouble(Setting.PriceForInfiniteJail), prisoner, player);
					prisoner.release();
				}
				else
				{
					Util.Message(jail.getSettings().getString(Setting.MessageJailPayNotEnoughMoneySelected), sender);
					return true;
				}
			}
			else
			{
				double releasebill = jail.getSettings().getDouble(Setting.PricePerMinute) * Math.round(prisoner.getRemainingTimeMinutes());
				if (payment >= releasebill)
				{
					if (args.length > 1)
						Util.Message(jail.getSettings().getString(Setting.MessageJailPayPaidReleasedHim).replace("<Amount>", format(releasebill, prisoner)).replace("<Prisoner>", prisoner.getName()), sender);
					else
						Util.Message(jail.getSettings().getString(Setting.MessageJailPayPaidReleased).replace("<Amount>",  format(releasebill, prisoner)), sender);
					pay(releasebill, prisoner, player);
					prisoner.release();
				}
				else
				{
					int minutes = (int) Math.round(payment / jail.getSettings().getDouble(Setting.PricePerMinute));
					double bill = minutes * jail.getSettings().getDouble(Setting.PricePerMinute);
					int remain = (int) (Math.round(prisoner.getRemainingTimeMinutes()) - minutes);
					if (args.length > 1)
					{
						String message = jail.getSettings().getString(Setting.MessageJailPayPaidLoweredTimeHim);
						message = message.replace("<Amount>", format(bill, prisoner));
						message = message.replace("<Prisoner>", prisoner.getName());
						message = message.replace("<NewTime>", String.valueOf(remain));
						Util.Message(message, sender);
					}
						
					else
					{
						String message = jail.getSettings().getString(Setting.MessageJailPayPaidLoweredTime);
						message = message.replace("<Amount>", format(bill, prisoner));
						message = message.replace("<NewTime>", String.valueOf(remain));
						Util.Message(message, sender);
					}
					pay(bill, prisoner, player);
					prisoner.setRemainingTimeMinutes(remain);
				}
			}

		}
		return true;
	}
	
	private String format(double amount, JailPrisoner prisoner)
	{
		int currency = prisoner.getJail().getSettings().getInt(Setting.JailPayCurrency);
		if (currency == 0)
		{
			Plugin plugin = Jail.instance.getServer().getPluginManager().getPlugin("Register");
			if (plugin == null)
			{
				Jail.log.info("[Jail] You must have Register plugin installed to use JailPayCurrency = 0! See http://dev.bukkit.org/server-mods/register/");
				return String.valueOf(amount);
			}
			
			if (Methods.getMethod() != null)
			{
				return Methods.getMethod().format(amount);
			}
			else
			{
				Jail.log.info("[Jail] You must have economy plugin installed to use JailPayCurrency = 0!");
				return String.valueOf(amount);
			}
		}
		else
		{
			return String.valueOf((int) Math.ceil( amount ) * 1) + "x " + getMaterialName(Material.getMaterial(currency));
		}
	}
	
	private Boolean hasEnough(double amount, JailPrisoner prisoner, Player player)
	{
		int currency = prisoner.getJail().getSettings().getInt(Setting.JailPayCurrency);
		if (currency == 0)
		{
			Plugin plugin = Jail.instance.getServer().getPluginManager().getPlugin("Register");
			if (plugin == null)
			{
				Jail.log.info("[Jail] You must have Register plugin installed to use JailPayCurrency = 0! See http://dev.bukkit.org/server-mods/register/");
				return false;
			}

			
			if (Methods.getMethod() != null)
			{
				return Methods.getMethod().getAccount(player.getName()).hasEnough(amount);
			}
			else
			{
				Jail.log.info("[Jail] You must have economy plugin installed to use JailPayCurrency = 0!");
				return false;
			}
		}
		else
		{
			int items = 0;
			for (ItemStack i : player.getInventory().getContents())
			{
				if (i != null && i.getTypeId() == currency) items += i.getAmount();
			}
			
			return items >= amount;
		}
	}
	
	private void pay(double amount, JailPrisoner prisoner, Player player)
	{
		int currency = prisoner.getJail().getSettings().getInt(Setting.JailPayCurrency);
		if (currency == 0)
		{
			Plugin plugin = Jail.instance.getServer().getPluginManager().getPlugin("Register");
			if (plugin == null)
			{
				Jail.log.info("[Jail] You must have Register plugin installed to use JailPayCurrency = 0! See http://dev.bukkit.org/server-mods/register/");
				return;
			}

			
			if (Methods.getMethod() != null)
			{
				Methods.getMethod().getAccount(player.getName()).subtract(amount);
			}
			else
			{
				Jail.log.info("[Jail] You must have economy plugin installed to use JailPayCurrency = 0!");
			}
		}
		else
		{
			int amountneeded = (int) Math.ceil(amount);
			for (int i = 0; i < player.getInventory().getSize(); i++)
			{
				ItemStack item = player.getInventory().getItem(i);
				if (item == null || item.getTypeId() != currency) return;
				
				if (amountneeded >= item.getAmount())
				{
					amountneeded -= item.getAmount();
					player.getInventory().clear(i);
					
				}
				else
				{
					item.setAmount(item.getAmount() - amountneeded);
					player.getInventory().setItem(i, item);
					amountneeded = 0;
				}
				
				if (amountneeded == 0) break;
			}
		}

	}
	
	// Material name snippet by TechGuard
	private String getMaterialName(Material material){
        String name = material.toString();
        name = name.replaceAll("_", " ");
        if(name.contains(" ")){
            String[] split = name.split(" ");
            for(int i=0; i < split.length; i++){
                split[i] = split[i].substring(0, 1).toUpperCase()+split[i].substring(1).toLowerCase();
            }
            name = "";
            for(String s : split){
                name += " "+s;
            }
            name = name.substring(1);
        } else {
            name = name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
        }
        return name;
	}
}
