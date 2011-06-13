package com.matejdro.bukkit.jail;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class JailPlayer extends PlayerListener {
	private Jail plugin;

	public JailPlayer(Jail instance)
	{
		plugin = instance;
	}		
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getItemInHand().getTypeId() == Settings.SelectionTool)
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
			else if ( JailSetManager.players.containsKey(event.getPlayer().getName()))
			{
				JailSetManager.RightClick(event.getClickedBlock(), event.getPlayer());
				event.setCancelled(true);
			}
			
		}
	}
	
	public void onPlayerInteract(PlayerInteractEntityEvent event) {
			Player player = event.getPlayer();
			if (!InputOutput.jailStickParameters.containsKey(player.getItemInHand().getTypeId())) return;
			if (!Jail.permission(player, "jail.usejailstick" + String.valueOf(player.getItemInHand().getTypeId()), player.isOp())) return;
			
			String[] param = InputOutput.jailStickParameters.get(player.getItemInHand().getTypeId());
			
			List<Block> targets = player.getLineOfSight(null, Integer.parseInt(param[1]));
			targets.remove(0);
			Entity ent = event.getRightClicked();
			if (ent == null || !(ent instanceof Player)) return;
			Player target = (Player) ent;
			if (Jail.permission(target, "jail.canbestickjailed", true))
			{
				String args[] = new String[4];
				args[0] = target.getName();
				args[1] = param[2];
				args[2] = param[3];
				args[3] = param[4];
				plugin.PrepareJail((CommandSender) event.getPlayer(), args); 
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
						 plugin.Jail(prisoner, event.getPlayer());
					 }
					 else
					 {
						 plugin.UnJail(prisoner, event.getPlayer());
					 } 
				 }
				 else
				 {
					 plugin.Transfer(prisoner, event.getPlayer());
				 }
				 
			 }
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
