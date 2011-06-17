package com.matejdro.bukkit.jail.listeners;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.matejdro.bukkit.jail.InputOutput;
import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.JailZoneManager;
import com.matejdro.bukkit.jail.Settings;
import com.matejdro.bukkit.jail.Util;

public class JailBlockListener extends BlockListener {
	private Jail plugin;

	public JailBlockListener(Jail instance)
	{
		plugin = instance;
	}
	
	
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) return;
		if (Settings.BlockProtectionExceptions.contains(String.valueOf(event.getBlock().getTypeId()))) return;
		if (JailZoneManager.isInsideJail(event.getBlock().getLocation()) && (!Util.permission(event.getPlayer(), "jail.modifyjail", event.getPlayer().isOp()) || Jail.prisoners.containsKey(event.getPlayer().getName().toLowerCase())))
		{
			
			if (Settings.BlockDestroyPenalty > 0 && Jail.prisoners.containsKey(event.getPlayer().getName().toLowerCase()) && Jail.prisoners.get(event.getPlayer().getName().toLowerCase()).getRemainingTime() > 0)
				{
					JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName().toLowerCase());
					Util.Message(Settings.MessageDestroyPenalty, event.getPlayer());
					prisoner.setRemainingTime(prisoner.getRemainingTime() + Settings.BlockDestroyPenalty * 6);
					InputOutput.UpdatePrisoner(prisoner);
				}
			else
				{
					Util.Message(Settings.MessageDestroyNoPenalty, event.getPlayer());
				}
			event.setCancelled(true);
		}
	
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) return;
		if (Settings.BlockProtectionExceptions.contains(String.valueOf(event.getBlock().getTypeId()))) return;
		if (JailZoneManager.isInsideJail(event.getBlockPlaced().getLocation()) && (!Util.permission(event.getPlayer(), "jail.modifyjail", event.getPlayer().isOp()) || Jail.prisoners.containsKey(event.getPlayer().getName().toLowerCase())))
		{
			if (Settings.BlockPlacePenalty > 0 && Jail.prisoners.containsKey(event.getPlayer().getName()) && Jail.prisoners.get(event.getPlayer().getName()).getRemainingTime() > 0)
				{
					JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName());
					Util.Message(Settings.MessagePlacePenalty, event.getPlayer());
					prisoner.setRemainingTime(prisoner.getRemainingTime() + Settings.BlockPlacePenalty * 6);
					InputOutput.UpdatePrisoner(prisoner);
				}
			else
				{
					Util.Message(Settings.MessagePlaceNoPenalty, event.getPlayer());
				}
			event.setCancelled(true);
		}

	}
	
	public void onBlockIgnite(BlockIgniteEvent event)
	{
		if (event.isCancelled()) return;
		if (JailZoneManager.isInsideJail(event.getBlock().getLocation()))
		{
			if (event.getCause() == IgniteCause.FLINT_AND_STEEL)
			{
				if (Settings.FirePenalty > 0 && Jail.prisoners.containsKey(event.getPlayer().getName()) && Jail.prisoners.get(event.getPlayer().getName()).getRemainingTime() > 0)
				{
					JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName());
					Util.Message(Settings.MessageFirePenalty, event.getPlayer());
					prisoner.setRemainingTime(prisoner.getRemainingTime() + Settings.FirePenalty * 6);
					InputOutput.UpdatePrisoner(prisoner);
				}
			else
				{
					Util.Message(Settings.MessageFireNoPenalty, event.getPlayer());
				}
			}
			event.setCancelled(true);
		}
	}
}
