package com.matejdro.bukkit.jail.listeners;

import java.util.Random;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.JailZoneManager;
import com.matejdro.bukkit.jail.Settings;

public class JailEntityListener extends EntityListener {
	private Jail plugin;
	public JailEntityListener(Jail instance)
	{
		plugin = instance;
	}
	
	public void onEntityDeath(EntityDeathEvent event) {
		//Wolves have done their job, lets remove them.
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();	
			JailPrisoner prisoner = Jail.prisoners.get(player.getName());
			if (prisoner == null) return;
			
			for (Object o : prisoner.getGuards().toArray())
			{
				Wolf w = (Wolf) o;
				prisoner.getGuards().remove(w);
				w.remove();
			}
		}
		//Respawn wolf or release player if he killed all the wolves and config says so.
		else if (event.getEntity() instanceof Wolf)
		{
			Wolf wolf = (Wolf) event.getEntity();
			if (Jail.guards.containsKey(wolf))
			{
				JailPrisoner prisoner = Jail.guards.get(wolf);
				
				prisoner.getGuards().remove(wolf);
				Jail.guards.remove(wolf);
				
				Player player = plugin.getServer().getPlayer(prisoner.getName());
				if (player == null) return;
				
				if (Settings.RespawnGuards)
					prisoner.spawnGuards(1, player.getLocation(), player);
				else if (prisoner.getGuards().size() == 0)
				{
					plugin.getServer().broadcastMessage(player.getName() + " have escaped from jail!");
					prisoner.delete();
				}
					
				
					
			}
		}
		
	}
	
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled()) return;
		Entity victim = event.getEntity();
		// Apply Wolf Armor or Invincibiliy
		if (Jail.guards.containsKey(victim) && Settings.Guardinvincibility)
		{
				event.setCancelled(true);
				return;
		}
		if (!(event instanceof EntityDamageByEntityEvent)) return;
		EntityDamageByEntityEvent newevent = (EntityDamageByEntityEvent) event;
		Entity damager = newevent.getDamager();

		//Apply Wolf damage and damage speed change
		if (Jail.guards.containsKey(damager))
		{
			if (new Random().nextInt(100) > Settings.GuardAttackSpeedPercent) 
			{
				event.setCancelled(true);
				return;
			}
			event.setDamage(Settings.GuardDamage);
		}
		
		if (Settings.PreventPvPInJail && victim instanceof Player && damager instanceof Player && JailZoneManager.isInsideJail(victim.getLocation()))
		{
			event.setCancelled(true);
			return;
		}
			
	}
	
	public void onEntityExplode(EntityExplodeEvent event) {
		if (event.isCancelled()) return;
		for (Object o : event.blockList().toArray())
		{
			Block b = (Block) o;
			if (JailZoneManager.isInsideJail(b.getLocation()))
			{
				event.setCancelled(true);
				return;
			}
		}
	}
	
	
	
}
