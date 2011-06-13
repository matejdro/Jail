package com.matejdro.bukkit.jail;

import org.bukkit.Location;
import org.bukkit.Material;
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

public class JailPlayerPrevent extends PlayerListener {
	private Jail plugin;

	public JailPlayerPrevent(Jail instance)
	{
		plugin = instance;
	}
	
	public void onPlayerChat (PlayerChatEvent event)
	{
		if (event.isCancelled()) return;
		JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName().toLowerCase());
		if (prisoner != null && prisoner.isMuted())
		{
			Jail.Message(Settings.MessageMute, event.getPlayer());
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
						
						Jail.Message(Settings.MessageCommandPenalty, event.getPlayer());
						prisoner.setRemainingTime(prisoner.getRemainingTime() + Settings.CommandPenalty * 6);
						InputOutput.UpdatePrisoner(prisoner);
					}
				else
					{
						Jail.Message(Settings.MessageCommandNoPenalty, event.getPlayer());
					}
					event.setCancelled(true);
					return;
				}
			}
		}
		}
		
		 
	 public void onPlayerMove(PlayerMoveEvent event) {
		 //if (event.isCancelled()) return;
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
								if (Settings.GuardTeleportDistance > 0 && w.getLocation().distanceSquared(w.getTarget().getLocation()) > Settings.GuardTeleportDistance)
									w.teleport(w.getTarget().getLocation());
								if (w.getTarget() == null) w.setTarget(event.getPlayer());
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
							
							Jail.Message(Settings.MessageMovePenalty, event.getPlayer());
							prisoner.setRemainingTime(prisoner.getRemainingTime() + Settings.PlayerMovePenalty * 6);
							InputOutput.UpdatePrisoner(prisoner);
						}
					else
						{
							Jail.Message(Settings.MessageMoveNoPenalty, event.getPlayer());
						}	
					
					Location teleport;
					if (!Settings.AlwaysTeleportIntoJailCenter && jail.isInside(event.getFrom()))
						teleport = event.getFrom();
					else
						teleport = prisoner.getTeleportLocation();
					event.getPlayer().teleport(teleport);
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
		 onPlayerMove((PlayerMoveEvent) event);
		      }
	 
	 
	 public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		 if (plugin.isInsideJail(event.getBlockClicked().getLocation()) || plugin.isInsideJail(event.getBlockClicked().getFace(event.getBlockFace()).getLocation()))
		 {
			 if (Settings.BucketPenalty > 0 && Jail.prisoners.containsKey(event.getPlayer().getName().toLowerCase()) && Jail.prisoners.get(event.getPlayer().getName().toLowerCase()).getRemainingTime() > 0)
				{
					JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName().toLowerCase());
					Jail.Message(Settings.MessageBucketPenalty, event.getPlayer());
					prisoner.setRemainingTime(prisoner.getRemainingTime() + Settings.BucketPenalty * 6);
					InputOutput.UpdatePrisoner(prisoner);
				}
			else
				{
					Jail.Message(Settings.MessageBucketNoPenalty, event.getPlayer());
				}
			 event.setCancelled(true);
		 }
	 }
	 
	 public void onPlayerInteract(PlayerInteractEvent event) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.CHEST && (Jail.prisoners.containsKey(event.getPlayer().getName().toLowerCase()) || !Jail.permission(event.getPlayer(), "jail.openchest", event.getPlayer().isOp())))
				{
				for (JailZone jail : Jail.zones.values())
					for (JailCell cell : jail.getCellList())
					{
						if ((!Settings.CanPrisonerOpenHisChest || !cell.getPlayerName().equals(event.getPlayer().getName().toLowerCase()) && ((cell.getChest() != null && event.getClickedBlock() == cell.getChest().getBlock()) || (cell.getSecondChest() != null && event.getClickedBlock() == cell.getSecondChest().getBlock()))))
								{
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
							
							Jail.Message(Settings.MessageMovePenalty, event.getPlayer());
							prisoner.setRemainingTime(prisoner.getRemainingTime() + Settings.PlayerMovePenalty * 6);
							InputOutput.UpdatePrisoner(prisoner);
						}
					else
						{
							Jail.Message(Settings.MessageMoveNoPenalty, event.getPlayer());
						}
					event.setRespawnLocation(prisoner.getTeleportLocation());					
				}
			}

	 }

	 
	 
	
}
