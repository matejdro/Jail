package com.matejdro.bukkit.jail.commands;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailZoneCreation;
import com.matejdro.bukkit.jail.Util;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CuboidRegionSelector;

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
		if (plugin != null)
		{
			WorldEditPlugin we = (WorldEditPlugin) plugin;
			LocalPlayer player = new BukkitPlayer(we, we.getServerInterface(), (Player) sender);
			LocalSession session = we.getWorldEdit().getSession(player);
			if (!(session.getRegionSelector() instanceof CuboidRegionSelector))
			{
				Util.Message("Jail supports only cuboid regions!", sender);
				return true;
			}
			
			CuboidRegionSelector selector = (CuboidRegionSelector) session.getRegionSelector();
	
			try {
				CuboidRegion region = selector.getRegion();

				Vector v1 = region.getPos1();
				Block b1 = ((Player) sender).getWorld().getBlockAt(v1.getBlockX(), v1.getBlockY(), v1.getBlockZ());
				
				Vector v2 = region.getPos2();
				Block b2 = ((Player) sender).getWorld().getBlockAt(v2.getBlockX(), v2.getBlockY(), v2.getBlockZ());
				
				JailZoneCreation.selectstart((Player) sender, args[0].toLowerCase());
				JailZoneCreation.select((Player) sender, b1);
				JailZoneCreation.select((Player) sender, b2);
				
				return true;

			} catch (IncompleteRegionException e) {
				Util.Message("WorldEdit region is not fully selected!", sender);
				return true;
			}
		}
		else
		{
			Util.Message("WorldEdit is not installed!", sender);
		}
		
		return true;
		
	}

}
