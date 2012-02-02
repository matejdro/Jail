package com.matejdro.bukkit.jail.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionDefault;

import com.matejdro.bukkit.jail.InputOutput;
import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailCellCreation;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.JailZoneCreation;
import com.matejdro.bukkit.jail.PrisonerManager;
import com.matejdro.bukkit.jail.Setting;
import com.matejdro.bukkit.jail.Util;
import com.matejdro.bukkit.jail.commands.JailSetCommand;

public class JailPlayerListener extends PlayerListener {
	private Jail plugin;

	public JailPlayerListener(Jail instance)
	{
		plugin = instance;
	}		
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getItemInHand().getTypeId() == InputOutput.global.getInt(Setting.SelectionTool.getString(), 280))
		{
			if ( JailZoneCreation.players.containsKey(event.getPlayer().getName()))
			{
				JailZoneCreation.select(event.getPlayer(), event.getClickedBlock());
				event.setCancelled(true);
			}
			else if ( JailCellCreation.players.containsKey(event.getPlayer().getName()))
			{
				JailCellCreation.select(event.getPlayer(), event.getClickedBlock());
				event.setCancelled(true);
			}
			else if ( JailSetCommand.players.containsKey(event.getPlayer().getName()))
			{
				JailSetCommand.RightClick(event.getClickedBlock(), event.getPlayer());
				event.setCancelled(true);
			}
		}
		
		PrisonerManager.useJailStick(event.getPlayer());
	}
		

	
	@Override
	public void onPlayerAnimation(PlayerAnimationEvent event) {
		if (event.getAnimationType() == PlayerAnimationType.ARM_SWING)
		{
			PrisonerManager.useJailStick(event.getPlayer());
		}
	}

	public void onPlayerChat(PlayerChatEvent event) {
		if (event.isCancelled()) return;
		if ( JailCellCreation.players.containsKey(event.getPlayer().getName()))
		{
			if (JailCellCreation.chatmessage(event.getPlayer(), event.getMessage()));
				event.setCancelled(true);
		}
	}

	
	 public void onPlayerJoin(PlayerJoinEvent event) {
		 if (Jail.prisoners.containsKey(event.getPlayer().getName().toLowerCase()))
		 {
			 JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName().toLowerCase());
			 if (prisoner.offlinePending())
			 {
				 if (prisoner.getTransferDestination().isEmpty())
				 {
					 if (prisoner.getRemainingTime() != 0)
					 {
						 PrisonerManager.Jail(prisoner, event.getPlayer());
					 }
					 else if (prisoner.getJail() != null)
					 {
						 PrisonerManager.UnJail(prisoner, event.getPlayer());
						 return;
					 } 
					 else
					 {
						 prisoner.delete();
						 return;
					 }
				 }
				 else
				 {
					 PrisonerManager.Transfer(prisoner, event.getPlayer());
				 }
				 
			 }
			 
			 if (prisoner.getJail().getSettings().getBoolean(Setting.IgnorePrisonersSleepingState))
				 event.getPlayer().setSleepingIgnored(true);
		 }
	 }
	 
	 public void onPlayerQuit(PlayerQuitEvent event) {
		JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName().toLowerCase());
		if (prisoner == null) return;

		for (Object o : prisoner.getGuards().toArray())
		{
			Wolf w = (Wolf) o;
			prisoner.getGuards().remove(w);
			Jail.guards.remove(w);
			w.remove();
		}
	 }
}
