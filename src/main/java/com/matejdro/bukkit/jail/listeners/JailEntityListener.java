package com.matejdro.bukkit.jail.listeners;

import java.util.Random;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.JailZone;
import com.matejdro.bukkit.jail.JailZoneManager;
import com.matejdro.bukkit.jail.Setting;

public class JailEntityListener implements Listener {
	private Jail plugin;
	public JailEntityListener(Jail instance)
	{
		plugin = instance;
	}
	
	@EventHandler()
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
				
				if (prisoner.getJail().getSettings().getBoolean(Setting.RespawnGuards))
					prisoner.spawnGuards(1, player.getLocation(), player);
				else if (prisoner.getGuards().size() == 0)
				{
					plugin.getServer().broadcastMessage(player.getName() + " have escaped from jail!");
					prisoner.delete();
				}
					
				
					
			}
		}
		
	}
	
	@EventHandler()
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled()) return;
		Entity victim = event.getEntity();
		// Apply Wolf Armor or Invincibiliy
		JailPrisoner prisoner = Jail.guards.get(victim);
		JailZone jail = prisoner != null ? prisoner.getJail() : null;
		if (prisoner != null)
		{
			if (jail.getSettings().getBoolean(Setting.GuardInvincibility) )
			{
					event.setCancelled(true);
					return;
			}
			int newArmor = event.getDamage() - (event.getDamage() * jail.getSettings().getInt(Setting.GuardArmor) / 100);
			if (newArmor <= 0) newArmor = 1;
			event.setDamage(newArmor);
		}
		if (!(event instanceof EntityDamageByEntityEvent)) return;
		EntityDamageByEntityEvent newevent = (EntityDamageByEntityEvent) event;
		Entity damager = newevent.getDamager();
		
		prisoner = Jail.guards.get(damager);
		jail = prisoner != null ? prisoner.getJail() : null;

		//Apply Wolf damage and damage speed change
		if (prisoner != null)
		{
			if (new Random().nextInt(100) > jail.getSettings().getInt(Setting.GuardAttackSpeedPercent)) 
			{
				event.setCancelled(true);
				return;
			}
			event.setDamage(jail.getSettings().getInt(Setting.GuardDamage));
		}
		jail = JailZoneManager.getJail(victim.getLocation());
		if (jail != null && jail.getSettings().getBoolean(Setting.EnablePVPProtection) && victim instanceof Player && damager instanceof Player)
		{
			event.setCancelled(true);
			return;
		}
			
	}
	
	@EventHandler()
	public void onEntityExplode(EntityExplodeEvent event) {
		if (event.isCancelled()) return;
		for (Object o : event.blockList().toArray())
		{
			Block b = (Block) o;
			JailZone jail = JailZoneManager.getJail(b.getLocation());
			if (jail != null)
			{
				if (!jail.getSettings().getBoolean(Setting.EnableExplosionProtection)) return;
				event.setCancelled(true);
				return;
			}
		}
	}
	
	
	
}
