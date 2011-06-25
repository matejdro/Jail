package com.matejdro.bukkit.jail.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.matejdro.bukkit.jail.InputOutput;
import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailCell;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.JailZone;
import com.matejdro.bukkit.jail.JailZoneManager;
import com.matejdro.bukkit.jail.Settings;
import com.matejdro.bukkit.jail.Util;

public class JailPlayerProtectionListener extends PlayerListener {
	private Jail plugin;

	public JailPlayerProtectionListener(Jail instance)
	{
		plugin = instance;
	}
	
	public void onPlayerChat (PlayerChatEvent event)
	{
		if (event.isCancelled()) return;
		JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName().toLowerCase());
		if (prisoner != null && prisoner.isMuted())
		{
			Util.Message(Settings.MessageMute, event.getPlayer());
			event.setCancelled(true);
		}
	}
	
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled()) return;
		if (Jail.prisoners.containsKey(event.getPlayer().getName().toLowerCase()))
		{
			for (String i : Settings.PreventCommands)
			{
				if (event.getMessage().startsWith(i))
				{
					JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName().toLowerCase());
					if (Settings.CommandPenalty > 0 && prisoner.getRemainingTime() > 0)
					{
						
						Util.Message(Settings.MessageCommandPenalty, event.getPlayer());
						prisoner.setRemainingTime(prisoner.getRemainingTime() + Settings.CommandPenalty * 6);
						InputOutput.UpdatePrisoner(prisoner);
					}
				else
					{
						Util.Message(Settings.MessageCommandNoPenalty, event.getPlayer());
					}
					event.setCancelled(true);
					return;
				}
			}
		}
		}
		
		 
	 public void onPlayerMove(PlayerMoveEvent event) {
		 if (event.isCancelled()) return;
		 if (Jail.prisoners.containsKey(event.getPlayer().getName().toLowerCase()))
			{
				JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName().toLowerCase());
				if (prisoner.isBeingReleased()) return;
				
				JailZone jail = prisoner.getJail();
				if (!jail.isInside(event.getTo()))
				{
					if (Settings.PlayerMoveProtectionAction.equals("guards"))
					{
						if (prisoner.getGuards().size() > 0)
						{
							for (Wolf w : prisoner.getGuards())
							{
								if (w == null || w.isDead())
								{
									prisoner.getGuards().remove(w);
									Jail.guards.remove(w);
									continue;
								}
								if (w.getTarget() == null) w.setTarget(event.getPlayer());
								if (Settings.GuardTeleportDistance > 0 && w.getLocation().distanceSquared(w.getTarget().getLocation()) > Settings.GuardTeleportDistance)
									w.teleport(w.getTarget().getLocation());
							}
						}
						else
							prisoner.spawnGuards(Settings.NumberOfGuards, event.getTo(), event.getPlayer());

					}
					
					else if (Settings.PlayerMoveProtectionAction.equals("escape"))
					{
						prisoner.delete();
						plugin.getServer().broadcastMessage(event.getPlayer().getName() + " have escaped from jail!");
						return;
					}
					else 
					{
						if (Settings.PlayerMovePenalty > 0  && prisoner.getRemainingTime() > 0)
						{
							
							Util.Message(Settings.MessageMovePenalty, event.getPlayer());
							prisoner.setRemainingTime(prisoner.getRemainingTime() + Settings.PlayerMovePenalty * 6);
							InputOutput.UpdatePrisoner(prisoner);
						}
					else
						{
							Util.Message(Settings.MessageMoveNoPenalty, event.getPlayer());
						}	
					
					Location teleport;
					if (!Settings.AlwaysTeleportIntoJailCenter && jail.isInside(event.getFrom()))
						teleport = event.getFrom();
					else
						teleport = prisoner.getTeleportLocation();
					event.setTo(teleport);
					event.setCancelled(true);
					}					
				}
				else if (Settings.PlayerMoveProtectionAction.equals("guards"))
				{
						for (Object o : prisoner.getGuards().toArray())
						{
								Wolf w = (Wolf) o;
								prisoner.getGuards().remove(w);
								Jail.guards.remove(w);
								w.remove();
						}
				}
			}
				
	 }
	 
	 
	 
	 public void onPlayerTeleport(PlayerTeleportEvent event) {
		 if (event.isCancelled()) return;
		 onPlayerMove((PlayerMoveEvent) event);
		      }
	 
	 
	 public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		 if (event.isCancelled()) return;
		 if (JailZoneManager.isInsideJail(event.getBlockClicked().getLocation()) || JailZoneManager.isInsideJail(event.getBlockClicked().getFace(event.getBlockFace()).getLocation()))
		 {
			 if (Settings.BucketPenalty > 0 && Jail.prisoners.containsKey(event.getPlayer().getName().toLowerCase()) && Jail.prisoners.get(event.getPlayer().getName().toLowerCase()).getRemainingTime() > 0)
				{
					JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName().toLowerCase());
					Util.Message(Settings.MessageBucketPenalty, event.getPlayer());
					prisoner.setRemainingTime(prisoner.getRemainingTime() + Settings.BucketPenalty * 6);
					InputOutput.UpdatePrisoner(prisoner);
				}
			else
				{
					Util.Message(Settings.MessageBucketNoPenalty, event.getPlayer());
				}
			 event.setCancelled(true);
		 }
	 }
	 
	 public void onPlayerInteract(PlayerInteractEvent event) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.CHEST && (Jail.prisoners.containsKey(event.getPlayer().getName().toLowerCase()) || !Util.permission(event.getPlayer(), "jail.openchest", event.getPlayer().isOp())))
				{
				for (JailZone jail : Jail.zones.values())
					for (JailCell cell : jail.getCellList())
					{
						if ((!Settings.CanPrisonerOpenHisChest || !cell.getPlayerName().equals(event.getPlayer().getName().toLowerCase())) && ((cell.getChest() != null && event.getClickedBlock() == cell.getChest().getBlock()) || (cell.getSecondChest() != null && event.getClickedBlock() == cell.getSecondChest().getBlock())))
								{
							event.setCancelled(true);
							return;
						}
					}
				}
			
			JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName().toLowerCase());
			if (prisoner != null)
			{
				if (event.getClickedBlock() != null)
				{
					int id = event.getClickedBlock().getTypeId();
					if (Settings.PreventInteractionBlocks.contains(String.valueOf(id)))
					{
						if (event.getAction() != Action.PHYSICAL && Settings.InteractionPenalty > 0  && prisoner.getRemainingTime() > 0)
						{
							
							Util.Message(Settings.MessageInteractionPenalty, event.getPlayer());
							prisoner.setRemainingTime(prisoner.getRemainingTime() + Settings.InteractionPenalty * 6);
							InputOutput.UpdatePrisoner(prisoner);
						}
						else if (event.getAction() != Action.PHYSICAL)
						{
							Util.Message(Settings.MessageInteractionNoPenalty, event.getPlayer());
						}
						
						event.setCancelled(true);
						return;
					}
				}
				if (event.getPlayer().getItemInHand() != null)
				{
					int id = event.getPlayer().getItemInHand().getTypeId();
					if (Settings.PreventInteractionItems.contains(String.valueOf(id)))
					{
						if (event.getAction() != Action.PHYSICAL && Settings.InteractionPenalty > 0  && prisoner.getRemainingTime() > 0)
						{
							
							Util.Message(Settings.MessageInteractionPenalty, event.getPlayer());
							prisoner.setRemainingTime(prisoner.getRemainingTime() + Settings.InteractionPenalty * 6);
							InputOutput.UpdatePrisoner(prisoner);
						}
						else if (event.getAction() != Action.PHYSICAL)
						{
							Util.Message(Settings.MessageInteractionNoPenalty, event.getPlayer());
						}
						
						event.setCancelled(true);
						return;
					}
				}

			}
		}
	 
	 public void onPlayerRespawn(PlayerRespawnEvent event) {
		 if (Jail.prisoners.containsKey(event.getPlayer().getName().toLowerCase()) && !Jail.prisoners.get(event.getPlayer().getName().toLowerCase()).isBeingReleased())
			{
				JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName().toLowerCase());
				
				JailZone jail = prisoner.getJail();
				if (!jail.isInside(event.getRespawnLocation()))
				{
					if (Settings.PlayerMovePenalty > 0  && prisoner.getRemainingTime() > 0)
						{
							
							Util.Message(Settings.MessageMovePenalty, event.getPlayer());
							prisoner.setRemainingTime(prisoner.getRemainingTime() + Settings.PlayerMovePenalty * 6);
							InputOutput.UpdatePrisoner(prisoner);
						}
					else
						{
							Util.Message(Settings.MessageMoveNoPenalty, event.getPlayer());
						}
					final Location teleloc = prisoner.getTeleportLocation();
					final Player player = event.getPlayer();
					Jail.instance.getServer().getScheduler().scheduleSyncDelayedTask(Jail.instance, new Runnable() {

					    public void run() {
					        if (player != null)
					        	player.teleport(teleloc);
					    }
					}, 1);
				}
			}

	 }

	 
	 
	
}
