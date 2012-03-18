package com.matejdro.bukkit.jail;

import java.util.logging.Level;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CuboidRegionSelector;

public class WorldEditHandler {
    @SuppressWarnings("CallToThreadDumpStack")
	 public static Block[] getWorldEditRegion(Player bplayer)
	    {
	    	Plugin plugin = Jail.instance.getServer().getPluginManager().getPlugin("WorldEdit");
			if (plugin != null)
			{
				WorldEditPlugin we = (WorldEditPlugin) plugin;
				LocalPlayer player = new BukkitPlayer(we, we.getServerInterface(), (Player) bplayer);
				LocalSession session = we.getWorldEdit().getSession(player);
				if (!(session.getRegionSelector() instanceof CuboidRegionSelector))
				{
					Util.Message("Jail supports only cuboid regions!", bplayer);
					return null;
				}
				
				CuboidRegionSelector selector = (CuboidRegionSelector) session.getRegionSelector();
		
				try {
					CuboidRegion region = selector.getRegion();

					Block[] corners = new Block[2];
					
					Vector v1 = region.getPos1();
					corners[0] = bplayer.getWorld().getBlockAt(v1.getBlockX(), v1.getBlockY(), v1.getBlockZ());
					
					Vector v2 = region.getPos2();
					corners[1] = bplayer.getWorld().getBlockAt(v2.getBlockX(), v2.getBlockY(), v2.getBlockZ());
									
					return corners;

				} catch (Exception e) {
					
					if (e instanceof IncompleteRegionException)
						Util.Message("WorldEdit region is not fully selected!", bplayer);
					else
					{
						Jail.log.log(Level.SEVERE, "[Jail] Error while retreiving WorldEdit region! - {0}", e.getMessage());
						e.printStackTrace();
					}
					return null;
				}
			}
			else
			{
				Util.Message("WorldEdit is not installed!", bplayer);
			}
			
			return null;
	    }
}
