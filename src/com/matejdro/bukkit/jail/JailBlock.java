package com.matejdro.bukkit.jail;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class JailBlock extends BlockListener {
	private Jail plugin;

	public JailBlock(Jail instance)
	{
		plugin = instance;
	}
	
	
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) return;
		if (plugin.isInsideJail(event.getBlock().getLocation()) && (!Jail.permission(event.getPlayer(), "jail.command.jailcreate", event.getPlayer().isOp()) || Jail.prisoners.containsKey(event.getPlayer().getName().toLowerCase())))
		{
			
			if (Settings.BlockDestroyPenalty > 0 && Jail.prisoners.containsKey(event.getPlayer().getName().toLowerCase()) && Jail.prisoners.get(event.getPlayer().getName().toLowerCase()).getRemainingTime() > 0)
				{
					JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName().toLowerCase());
					Jail.Message(Settings.MessageDestroyPenalty, event.getPlayer());
					prisoner.setRemainingTime(prisoner.getRemainingTime() + Settings.BlockDestroyPenalty * 6);
					InputOutput.UpdatePrisoner(prisoner);
				}
			else
				{
					Jail.Message(Settings.MessageDestroyNoPenalty, event.getPlayer());
				}
			event.setCancelled(true);
		}
	
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) return;
		if (plugin.isInsideJail(event.getBlockPlaced().getLocation()) && (!Jail.permission(event.getPlayer(), "jail.command.jailcreate", event.getPlayer().isOp()) || Jail.prisoners.containsKey(event.getPlayer().getName())))
		{
			
			if (Settings.BlockPlacePenalty > 0 && Jail.prisoners.containsKey(event.getPlayer().getName()) && Jail.prisoners.get(event.getPlayer().getName()).getRemainingTime() > 0)
				{
					JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName());
					Jail.Message(Settings.MessagePlacePenalty, event.getPlayer());
					prisoner.setRemainingTime(prisoner.getRemainingTime() + Settings.BlockPlacePenalty * 6);
					InputOutput.UpdatePrisoner(prisoner);
				}
			else
				{
					Jail.Message(Settings.MessagePlaceNoPenalty, event.getPlayer());
				}
			event.setCancelled(true);
		}

	}
	
	public void onBlockIgnite(BlockIgniteEvent event)
	{
		if (plugin.isInsideJail(event.getBlock().getLocation()))
		{
			if (event.getCause() == IgniteCause.FLINT_AND_STEEL)
			{
				if (Settings.FirePenalty > 0 && Jail.prisoners.containsKey(event.getPlayer().getName()) && Jail.prisoners.get(event.getPlayer().getName()).getRemainingTime() > 0)
				{
					JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName());
					Jail.Message(Settings.MessageFirePenalty, event.getPlayer());
					prisoner.setRemainingTime(prisoner.getRemainingTime() + Settings.FirePenalty * 6);
					InputOutput.UpdatePrisoner(prisoner);
				}
			else
				{
					Jail.Message(Settings.MessageFireNoPenalty, event.getPlayer());
				}
			}
			event.setCancelled(true);
		}
	}
}
