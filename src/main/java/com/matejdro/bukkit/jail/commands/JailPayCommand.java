package com.matejdro.bukkit.jail.commands;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.JailZone;
import com.matejdro.bukkit.jail.Setting;
import com.matejdro.bukkit.jail.Settings;
import com.matejdro.bukkit.jail.Util;

public class JailPayCommand extends BaseCommand {
	
    public static Economy economy = null;
	
	public JailPayCommand()
	{
		needPlayer = true;
		adminCommand = false;
		permission = "jail.usercmd.jailpay";
	}


	public Boolean run(CommandSender sender, String[] args) {		
		Player player = (Player) sender;
		
		JailPrisoner prisoner = Jail.prisoners.get(((Player) sender).getName().toLowerCase());
//		if (prisoner == null || !prisoner.getJail().getSettings().getBoolean(Setting.EnablePaying)) 
//		{
//			return false;
//		}
		
		if (args.length < 1)
		{
			if (prisoner == null) 
			{
				Util.Message(Settings.getGlobalString(Setting.MessageYouNotJailed), sender);
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
			if (args.length > 1)
			{
				prisoner = Jail.prisoners.get(args[1].toLowerCase());
				
				if (prisoner == null)
				{
					Util.Message(Settings.getGlobalString(Setting.MessagePlayerNotJailed), sender);
					return true;
				}	
			}
			else
			{
				prisoner = Jail.prisoners.get(((Player) sender).getName().toLowerCase());
				if (prisoner == null) 
				{
					Util.Message(Settings.getGlobalString(Setting.MessageYouNotJailed), sender);
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
						
			double payment = Math.max(0.0, Double.parseDouble(args[0]));
			
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
			Plugin plugin = Jail.instance.getServer().getPluginManager().getPlugin("Vault");
			if (plugin == null)
			{
				Jail.log.info("[Jail] You must have Vault plugin installed to use JailPayCurrency = 0! See http://dev.bukkit.org/server-mods/vault");
				return String.valueOf(amount);
			}
			
			if (setupEconomy())
			{
				return economy.format(amount);
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
			Plugin plugin = Jail.instance.getServer().getPluginManager().getPlugin("Vault");
			if (plugin == null)
			{
				Jail.log.info("[Jail] You must have Vault plugin installed to use JailPayCurrency = 0! See http://dev.bukkit.org/server-mods/vault");
				return false;
			}
			
			if (setupEconomy())
			{
				return economy.has(player.getName(), amount);
			}
			else
			{
				Jail.log.info("[Jail] You must have economy plugin installed to use JailPayCurrency = 0!");
				return false;
			}
		}
		else
		{
			return player.getInventory().contains(currency, (int) Math.ceil(amount));
		}
	}
	
	private void pay(double amount, JailPrisoner prisoner, Player player)
	{
		int currency = prisoner.getJail().getSettings().getInt(Setting.JailPayCurrency);
		if (currency == 0)
		{
			Plugin plugin = Jail.instance.getServer().getPluginManager().getPlugin("Vault");
			if (plugin == null)
			{
				Jail.log.info("[Jail] You must have Vault plugin installed to use JailPayCurrency = 0! See http://dev.bukkit.org/server-mods/vault");
				return;
			}
			
			if (setupEconomy())
			{
				economy.withdrawPlayer(player.getName(), amount);
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
				if (item == null || item.getTypeId() != currency) continue;
				
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
	
	private Boolean setupEconomy()
    {
		if (economy != null) return true;
		
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}
