package com.matejdro.bukkit.jail.listeners;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.permissions.PermissionDefault;

import com.matejdro.bukkit.jail.InputOutput;
import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.JailZone;
import com.matejdro.bukkit.jail.JailZoneManager;
import com.matejdro.bukkit.jail.Setting;
import com.matejdro.bukkit.jail.Settings;
import com.matejdro.bukkit.jail.Util;

public class JailBlockListener extends BlockListener {
	
	
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) return;
		JailZone jail = JailZoneManager.getJail(event.getBlock().getLocation());
		if (jail == null || !jail.getSettings().getBoolean(Setting.EnableBlockDestroyProtection)) return;
		
		if (jail.getSettings().getList(Setting.BlockProtectionExceptions).contains(String.valueOf(event.getBlock().getTypeId()))) return;
		if ((!Util.permission(event.getPlayer(), "jail.modifyjail", PermissionDefault.OP) || Jail.prisoners.containsKey(event.getPlayer().getName().toLowerCase())))
		{
			
			if (jail.getSettings().getInt(Setting.BlockDestroyPenalty) > 0 && Jail.prisoners.containsKey(event.getPlayer().getName().toLowerCase()) && Jail.prisoners.get(event.getPlayer().getName().toLowerCase()).getRemainingTime() > 0)
				{
					JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName().toLowerCase());
					Util.Message(jail.getSettings().getString(Setting.MessageBlockDestroyedPenalty), event.getPlayer());
					prisoner.setRemainingTime(prisoner.getRemainingTime() + jail.getSettings().getInt(Setting.BlockDestroyPenalty) * 6);
					InputOutput.UpdatePrisoner(prisoner);
				}
			else
				{
					Util.Message(jail.getSettings().getString(Setting.MessageBlockDestroyedNoPenalty), event.getPlayer());
				}
			event.setCancelled(true);
		}
	
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) return;
		
		JailZone jail = JailZoneManager.getJail(event.getBlock().getLocation());
		if (jail == null || !jail.getSettings().getBoolean(Setting.EnableBlockPlaceProtection)) return;
		
		if (jail.getSettings().getList(Setting.BlockProtectionExceptions).contains(String.valueOf(event.getBlock().getTypeId()))) return;
		if (JailZoneManager.isInsideJail(event.getBlockPlaced().getLocation()) && (!Util.permission(event.getPlayer(), "jail.modifyjail", PermissionDefault.OP) || Jail.prisoners.containsKey(event.getPlayer().getName().toLowerCase())))
		{
			if (jail.getSettings().getInt(Setting.BlockPlacePenalty) > 0 && Jail.prisoners.containsKey(event.getPlayer().getName()) && Jail.prisoners.get(event.getPlayer().getName()).getRemainingTime() > 0)
				{
					JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName());
					Util.Message(jail.getSettings().getString(Setting.MessageBlockPlacedPenalty), event.getPlayer());
					prisoner.setRemainingTime(prisoner.getRemainingTime() + jail.getSettings().getInt(Setting.BlockPlacePenalty) * 6);
					InputOutput.UpdatePrisoner(prisoner);
				}
			else
				{
					Util.Message(jail.getSettings().getString(Setting.MessageBlockPlacedPenalty), event.getPlayer());
				}
			event.setCancelled(true);
		}

	}	
}
