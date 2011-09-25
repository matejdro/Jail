package com.matejdro.bukkit.jail;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.SpoutManager;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.CuboidRegionSelector;


public class Util {
	public static void Message(String message, Player player)
	{
		String color = "\u00A7f";
		final int maxLength = 61; //Max length of chat text message
        final String newLine = "[NEWLINE]";
        ArrayList<String> chat = new ArrayList<String>();
        chat.add(0, color);
        String[] words = message.split(" ");
        int lineNumber = 0;
        for (int i = 0; i < words.length; i++) {
                if (chat.get(lineNumber).length() + words[i].length() < maxLength && !words[i].equals(newLine)) {
                        chat.set(lineNumber, chat.get(lineNumber) + " " + words[i]);
                }
                else {
                        lineNumber++;
                        if (!words[i].equals(newLine)) {
                                chat.add(lineNumber, color + words[i]);
                        }
                        else
                                chat.add(lineNumber,color);
                }
        }
        for (int i = 0; i < chat.size(); i++) {
                player.sendMessage(chat.get(i));
        }
	}
	
	public static void Message(String message, CommandSender sender)
	{
		if (sender == null) return;
		if (sender instanceof Player)
		{
			Message(message, (Player) sender);
		}
		else
		{
			sender.sendMessage(message);
		}
	}
	
    public static Boolean permission(Player player, String line, PermissionDefault def)
    {
    	    if(Jail.permissions != null) {
    	    	return (((Permissions) Jail.permissions).getHandler()).has(player, line);
    	    } else {
    	    	return player.hasPermission(new Permission(line, def));
    	    }
    }
    
    public static int getNumberOfOccupiedItemSlots(ItemStack[] items)
    {
    	int size = 0;
    	for (ItemStack i : items)
    	{
    		if (i != null) size++;
    	}
    	return size;
    }
    
    public static Boolean isInteger(String text) {
    	  try {
    	    Integer.parseInt(text);
    	    return true;
    	  } catch (NumberFormatException e) {
    	    return false;
    	  }
    	}
    
    public static void log(String text)
    {
		try {
			FileWriter fstream = new FileWriter(new File("plugins" + File.separator + "Jail","jail.log"), true);
			BufferedWriter out = new BufferedWriter(fstream);
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
			out.append("[" + dateFormat.format(new Date()) + "] " + text);
	    	out.newLine();
	    	  //Close the output stream
	    	out.close();
	    	fstream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Jail.log.log(Level.SEVERE, "[Jail]: Unable to write data to log file.");

			e.printStackTrace();
		}
    	 
    }
    
    public static void changeSkin(Player player, String skin)
	{
		Plugin plugin = Jail.instance.getServer().getPluginManager().getPlugin("Spout");
		if (plugin != null)
		{			
			if (!skin.trim().isEmpty())
				SpoutManager.getAppearanceManager().setGlobalSkin(player, skin);
			else
				SpoutManager.getAppearanceManager().resetGlobalSkin(player);
		}
	}
        
    public static void setPermissionsGroups(String playerName, List<String> groups)
    {
    	Plugin plugin = Jail.instance.getServer().getPluginManager().getPlugin("PermissionsEx");
    	if (plugin != null)
    	{
    		PermissionManager pex = PermissionsEx.getPermissionManager();
    		PermissionUser user = pex.getUser(playerName);
    		
    		user.setGroups(groups.toArray(new String[0]));
    		return;
    	}
    	
    	plugin = Jail.instance.getServer().getPluginManager().getPlugin("PermissionsBukkit");
		if (plugin == null) 
		{
			Jail.log.info("[Jail]You cannot use permission changing feature without PermissionsBukkit or PermissionsEx plugin!");
			return;
		}
		
		String gstring = "";
		for (String s : groups)
			gstring += s + ",";
		
		CraftServer cs = (CraftServer) Jail.instance.getServer();
		CommandSender coms = new ConsoleCommandSender(Jail.instance.getServer());
		cs.dispatchCommand(coms,"permissions player setgroup " + playerName + " " + gstring );
		
    }
    
    public static List<String> getPermissionsGroups(String playerName)
    {
    	Plugin plugin = Jail.instance.getServer().getPluginManager().getPlugin("PermissionsEx");
    	if (plugin != null)
    	{
    		PermissionManager pex = PermissionsEx.getPermissionManager();
    		PermissionUser user = pex.getUser(playerName);
    		
    		return Arrays.asList(user.getGroupsNames());
    	}

    	
    	plugin = Jail.instance.getServer().getPluginManager().getPlugin("PermissionsBukkit");
		if (plugin == null) return new ArrayList<String>();
			
		PermissionsPlugin pb = (PermissionsPlugin) plugin;
		
		List<String> groups = new ArrayList<String>();
		
		for (Group g : pb.getPlayerInfo(playerName).getGroups())
			groups.add(g.getName());
		return groups;
    }
    
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

			} catch (IncompleteRegionException e) {
				Util.Message("WorldEdit region is not fully selected!", bplayer);
				return null;
			}
		}
		else
		{
			Util.Message("WorldEdit is not installed!", bplayer);
		}
		
		return null;
    }
    
    public static Boolean isServer18()
    {
    	String ver = Jail.instance.getServer().getVersion();
    	ver = ver.substring(ver.indexOf("(MC: ") + 5, ver.indexOf("(MC: ") + 8);
    	return ver.equals("1.8");
    }
    

}
