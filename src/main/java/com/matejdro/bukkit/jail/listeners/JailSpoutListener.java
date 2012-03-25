package com.matejdro.bukkit.jail.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;

import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailPrisoner;
import com.matejdro.bukkit.jail.Setting;
import com.matejdro.bukkit.jail.Util;

public class JailSpoutListener implements Listener {
	
	@EventHandler()
    public void onSpoutCraftEnable(SpoutCraftEnableEvent event) {
    	JailPrisoner prisoner = Jail.prisoners.get(event.getPlayer().getName().toLowerCase());
    	if (prisoner != null && prisoner.getJail().getSettings().getBoolean(Setting.SpoutChangeSkin))
    			Util.changeSkin(event.getPlayer(), prisoner.getJail().getSettings().getString(Setting.SpoutSkinChangeURL));
		

    }
}
