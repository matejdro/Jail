package com.matejdro.bukkit.jail;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.Timer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.iConomy.iConomy;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Jail extends JavaPlugin {
	public static Logger log = Logger.getLogger("Minecraft");

	private JailPlayer PlayerListener;
	private JailBlock BlockListener;
	private JailPlayerPrevent PlayerPreventListener;
	private JailEntity EntityListener;
	public JailAPI API;
	private InputOutput IO;
	public static HashMap<String,JailZone> zones = new HashMap<String,JailZone>();
	public static HashMap<String,JailPrisoner> prisoners = new HashMap<String,JailPrisoner>();
	public static HashMap<Wolf, JailPrisoner> guards = new HashMap<Wolf, JailPrisoner>();
	private Timer timer;
	private int UpdateTime;
	
	public static Jail instance;
	
	public static Plugin permissions = null;

	
	//public Jail(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		//super(pluginLoader, instance, desc, folder, plugin, cLoader);
		
		
      // }
	

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		InputOutput.freeConnection();
		for (Wolf w : guards.keySet())
			w.remove();
	}

	@Override
	public void onEnable() {
		instance = this;
		
		PlayerListener = new JailPlayer(this);
		BlockListener = new JailBlock(this);
		PlayerPreventListener = new JailPlayerPrevent(this);
		EntityListener = new JailEntity(this);
		IO = new InputOutput(this);
		API = new JailAPI(this);
		UpdateTime = 0;
		
		IO.LoadSettings();
		IO.PrepareDB();
		IO.LoadJails();
		IO.LoadPrisoners();
		IO.LoadCells();
		
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, PlayerPreventListener, Event.Priority.Lowest, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, PlayerListener, Event.Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, PlayerListener, Event.Priority.Low, this);
		
		if (Settings.BlockDestroyProtection)
			getServer().getPluginManager().registerEvent(Event.Type.BLOCK_BREAK, BlockListener, Event.Priority.Low, this);
		if (Settings.PlayerMoveProtection)
		{
			getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, PlayerPreventListener, Event.Priority.Low, this);
			getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, PlayerPreventListener, Event.Priority.Low, this);
		}
			
		if (Settings.BlockPlaceProtection)
			getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACE, BlockListener, Event.Priority.Low, this);
		if (Settings.FireProtection)
			getServer().getPluginManager().registerEvent(Event.Type.BLOCK_IGNITE, BlockListener, Event.Priority.Low, this);
		if (Settings.BucketProtection)
			getServer().getPluginManager().registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, PlayerPreventListener, Event.Priority.Low, this);
		if (Settings.PlayerMoveProtectionAction.equals("guards"))
		{
			getServer().getPluginManager().registerEvent(Event.Type.PLAYER_RESPAWN, PlayerPreventListener, Event.Priority.Low, this);
			getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, PlayerListener, Event.Priority.Monitor, this);
			getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, EntityListener, Event.Priority.Monitor, this);
			getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, EntityListener, Event.Priority.Low, this);

		}
		
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, PlayerPreventListener, Event.Priority.Low, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, PlayerPreventListener, Event.Priority.Low, this);		
		timer = new Timer(1000,action);
		//timer.start();
		
		permissions = this.getServer().getPluginManager().getPlugin("Permissions");
	}
		
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
	
    public static Boolean permission(Player player, String line, Boolean def)
    {
    	    if(permissions != null) {
    	    	return (((Permissions) permissions).getHandler()).has(player, line);
    	    } else {
    	    	return def;
    	    }
    }

	
	public JailZone findNearestJail(Location loc)
	{
		JailZone jail = null;
		double len = -1;
		
		for (JailZone i : zones.values())
		{
			double clen = i.getDistance(loc);
			
			if (clen < len || len == -1)
			{
				len = clen;
				jail = i;
			}
				
		}
		
		return jail;
			
	}
	
	public JailZone findNearestJail(Location loc, String ignore)
	{
		JailZone jail = null;
		double len = -1;
		
		for (JailZone i : zones.values())
		{
			if (i.getName().equalsIgnoreCase(ignore)) continue;
			double clen = i.getDistance(loc);
			
			if (clen < len || len == -1)
			{
				len = clen;
				jail = i;
			}
				
		}
		
		return jail;
			
	}
	
	public Boolean isInsideJail(Location loc)
	{
		for (JailZone zone : zones.values())
		{
			if (zone.isInside(loc))
			{
				return true;
			}
		}
		return false;
	}
	
	public void PrepareJail(CommandSender sender, String args[])
	{
		String playername;
		int time = -1;
		String jailname = "";
		if (args.length < 1)
		{
			if (sender != null) Jail.Message("Usage: /jail [Name] (Time) (Jail Name) (Reason)", sender);
			return;
		}
		if (Jail.zones.size() < 1)
		{
			if (sender != null) Jail.Message("There is no jail available. Build one, before you can jail anyone!", sender);
			return;
		}
		if (Jail.prisoners.containsKey(args[0].toLowerCase()))
		{
			JailPrisoner prisoner = Jail.prisoners.get(args[0].toLowerCase());
			Player player = getServer().getPlayer(prisoner.getName());
			if (player != null)
			{
				player.teleport(prisoner.getTeleportLocation());
				if (sender != null) Jail.Message("Player was teleported back to his jail!", sender);

			}
			else
			{
				if (sender != null) Jail.Message("That player is already jailed!", sender);

			}
			return;
		}
		playername = args[0].toLowerCase();
		if (args.length > 1)
			time = Integer.valueOf(args[1]);
		if (args.length > 2)
			jailname = args[2].toLowerCase();
		String reason = "";
		if (args.length > 3)
		{
			for (int i=3;i<args.length;i++)
			{
				reason+= " " + args[i];
			}
			if (reason.length() > 250)
			{
				if (sender != null) Jail.Message("Reason is too long!", sender);
				return;
			}
		}
			
		if (jailname.equals(Settings.NearestJailCode)) 
			jailname = "";
		
		String jailer;
		if (sender instanceof Player)
			jailer = ((Player) sender).getName();
		else
			jailer = "console";
			
		Player player = null;
		for (Player i : getServer().getOnlinePlayers())
		{
			if (i.getName().equalsIgnoreCase(playername))
				player = i;
		}
		
		if (player == null)
		{
			JailPrisoner prisoner = new JailPrisoner(playername, time * 6, jailname, true, "", reason, Settings.AutomaticMute,  "" ,jailer);
			InputOutput.InsertPrisoner(prisoner);
			Jail.prisoners.put(prisoner.getName(), prisoner);
			Jail.Message("Player is offline. He will be automatically jailed when he connnects.", sender);
			
		}
		else
		{
			JailPrisoner prisoner = new JailPrisoner(playername, time * 6, jailname, false, "", reason, Settings.AutomaticMute,  "", jailer);
			Jail(prisoner, player);
			Jail.Message("Player jailed.", sender);
			
		}
	}
	
	public void Jail(JailPrisoner prisoner, Player player)
	{
		prisoner.SetBeingReleased(true);
		JailZone jail = prisoner.getJail();
		if (jail == null)
		{
			jail = findNearestJail(player.getLocation());
			prisoner.setJail(jail);
		}
		prisoner.setOfflinePending(false);
		if (prisoner.getReason().isEmpty())
			Message(Settings.MessageJail, player);
		else
			Message(Settings.MessageJailReason.replace("<Reason>", prisoner.getReason()), player);

		if (Settings.DeleteInventoryOnJail) player.getInventory().clear();

		
		JailCell cell = jail.getEmptyCell();
		if (cell != null)
		{
			cell.setPlayerName(player.getName());
			prisoner.setCell(cell);
			prisoner.updateSign();
			if (Settings.StoreInventory && cell.getChest() != null)
			{
				Chest chest = cell.getChest();
				chest.getInventory().clear();
				for (int i = 0;i<player.getInventory().getSize();i++)
				{
					if (chest.getInventory().getSize() <= getSize(chest.getInventory().getContents())) break;
					if (player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType() == Material.AIR) continue;
					chest.getInventory().addItem(player.getInventory().getItem(i));
					player.getInventory().clear(i);
				}
								
				if (cell.getSecondChest() != null)
				{
					chest = cell.getSecondChest();
					chest.getInventory().clear();
					for (int i = 0;i<player.getInventory().getSize();i++)
					{
						if (chest.getInventory().getSize() <= getSize(chest.getInventory().getContents())) break;
						if (player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType() == Material.AIR) continue;
						chest.getInventory().addItem(player.getInventory().getItem(i));
						player.getInventory().clear(i);
					}

				}
			}
			InputOutput.UpdateCell(cell);
		}
		
		player.teleport(prisoner.getTeleportLocation());
		if (Settings.StoreInventory) 
		{
			prisoner.storeInventory(player.getInventory());
			player.getInventory().clear();
		}
		
		if (prisoners.containsKey(prisoner.getName()))
			InputOutput.UpdatePrisoner(prisoner);
		else
			InputOutput.InsertPrisoner(prisoner);
		prisoners.put(prisoner.getName(), prisoner);
		prisoner.SetBeingReleased(false);
		
	}
	
	public void UnJail(JailPrisoner prisoner, Player player)
	{
		prisoner.SetBeingReleased(true);
		JailZone jail = prisoner.getJail();	
		Message(Settings.MessageUnjail, player);
		
		player.teleport(jail.getReleaseTeleportLocation());
		prisoner.delete();
		prisoners.remove(prisoner.getName());
		prisoner.SetBeingReleased(false);
		
		JailCell cell = prisoner.getCell();
		if (cell != null)
		{
			if (Settings.StoreInventory && cell.getChest() != null)
			{
				Chest chest = cell.getChest();
				for (int i = 0;i<chest.getInventory().getSize();i++)
				{
					if (chest.getInventory().getItem(i) == null || chest.getInventory().getItem(i).getType() == Material.AIR) continue;
					if (player.getInventory().firstEmpty() == -1)
						player.getWorld().dropItem(player.getLocation(), chest.getInventory().getItem(i));
					else
						player.getInventory().addItem(chest.getInventory().getItem(i));
				}
				chest.getInventory().clear();
				
				if (cell.getSecondChest() != null)
				{
					chest = cell.getSecondChest();
					for (int i = 0;i<chest.getInventory().getSize();i++)
					{
						if (chest.getInventory().getItem(i) == null || chest.getInventory().getItem(i).getType() == Material.AIR) continue;
						if (player.getInventory().firstEmpty() == -1)
							player.getWorld().dropItem(player.getLocation(), chest.getInventory().getItem(i));
						else
							player.getInventory().addItem(chest.getInventory().getItem(i));
					}
					chest.getInventory().clear();

				}
			}
			if (cell.getSign() != null)
			{
				Sign sign = cell.getSign();
				sign.setLine(0, "");
				sign.setLine(1, "");
				sign.setLine(2, "");
				sign.setLine(3, "");
				sign.update();

			}
			cell.setPlayerName("");
			InputOutput.UpdateCell(cell);
		}
		
		if (Settings.StoreInventory) prisoner.restoreInventory(player);
	}
		
	public void PrepareTransferAll(JailZone jail)
	{
		PrepareTransferAll(jail, "find nearest");
	}
	
	public void PrepareTransferAll(JailZone zone, String target)
	{
		for (JailPrisoner prisoner : zone.getPrisoners())
		{
			prisoner.setTransferDestination(target);
			Player player = null;
			for (Player i : getServer().getOnlinePlayers())
			{
				if (i.getName().equalsIgnoreCase(prisoner.getName()))
					player = i;
			}
			if (player == null)
			{
				
				prisoner.setOfflinePending(true);
				InputOutput.UpdatePrisoner(prisoner);
				Jail.prisoners.put(prisoner.getName(), prisoner);
				
			}
			else
			{
				Transfer(prisoner, player);
				
			}
		}
		
	}
	
	public void Transfer(JailPrisoner prisoner, Player player)
	{
		if (prisoner.getTransferDestination() == "find nearest") prisoner.setTransferDestination(findNearestJail(player.getLocation(), prisoner.getJail().getName()).getName());
		
		if (prisoner.getCell() != null)
		{
			Inventory inventory = player.getInventory();
			JailCell cell = prisoner.getCell();
			cell.setPlayerName("");
			if (cell.getSign() != null)
			{
				Sign sign = cell.getSign();
				sign.setLine(0, "");
				sign.setLine(1, "");
				sign.setLine(2, "");
				sign.setLine(3, "");
				sign.update();
			}
			
			if (cell.getChest() != null) 
			{
				for (ItemStack i: cell.getChest().getInventory().getContents())
					inventory.addItem(i);
				cell.getChest().getInventory().clear();
			}
			if (cell.getSecondChest() != null) 
			{
				for (ItemStack i: cell.getSecondChest().getInventory().getContents())
					inventory.addItem(i);
				cell.getSecondChest().getInventory().clear();
			}
			prisoner.setCell(null);
		}
						
		prisoner.SetBeingReleased(true);
		JailZone jail = zones.get(prisoner.getTransferDestination());
		prisoner.setJail(jail);
		prisoner.setTransferDestination("");
		prisoner.setOfflinePending(false);
		Message(Settings.MessageTransfer, player);
		prisoners.put(prisoner.getName(),prisoner);
		
		JailCell cell = jail.getEmptyCell();
		if (cell != null)
		{
			cell.setPlayerName(player.getName());
			prisoner.setCell(cell);
			prisoner.updateSign();
			if (Settings.StoreInventory && cell.getChest() != null)
			{
				Chest chest = cell.getChest();
				chest.getInventory().clear();
				for (int i = 0;i<player.getInventory().getSize();i++)
				{
					if (chest.getInventory().getSize() <= getSize(chest.getInventory().getContents())) break;
					if (player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType() == Material.AIR) continue;
					chest.getInventory().addItem(player.getInventory().getItem(i));
					player.getInventory().clear(i);
				}
								
				if (cell.getSecondChest() != null)
				{
					chest = cell.getSecondChest();
					chest.getInventory().clear();
					for (int i = 0;i<player.getInventory().getSize();i++)
					{
						if (chest.getInventory().getSize() <= getSize(chest.getInventory().getContents())) break;
						if (player.getInventory().getItem(i) == null || player.getInventory().getItem(i).getType() == Material.AIR) continue;
						chest.getInventory().addItem(player.getInventory().getItem(i));
						player.getInventory().clear(i);
					}

				}
			}
			InputOutput.UpdateCell(cell);
		}
		
		if (Settings.StoreInventory) 
		{
			prisoner.storeInventory(player.getInventory());
			player.getInventory().clear();
		}
		
		player.teleport(prisoner.getTeleportLocation());
		prisoner.SetBeingReleased(false);
		InputOutput.UpdatePrisoner(prisoner);
	}
	
	ActionListener action = new ActionListener ()
    {
      public void actionPerformed (ActionEvent event)
      {
    	  if (UpdateTime == 0)
    	  {
    		  synchronized(Jail.prisoners.values())
        	  {
        		  Object[] names = Jail.prisoners.keySet().toArray();
    	    	  for (Object prisonername : names)
    	    	  {
    	    		  JailPrisoner prisoner = Jail.prisoners.get(prisonername.toString());
    	    		  Player player = getServer().getPlayer(prisoner.getName());
    	    		  if (prisoner.getRemainingTime() > 0 && player != null)
    	    		  {
    	    			  prisoner.setRemainingTime(prisoner.getRemainingTime() - 1);
    	    			  if (prisoner.getRemainingTime() == 0 && prisoner.offlinePending() == false)
    	        		  {
    	        					UnJail(prisoner, player);
    	        		  }
    	    			  else
    	    			  {
    	        			  //Jail.prisoners.put(prisoner.name, prisoner);
    	        			  InputOutput.UpdatePrisoner(prisoner);   				  
    	    			  }
    	
    	    		  }
    	    	  }
        	  }
    		  UpdateTime++;
    		  
    	  
    	  }
    	  else
    	  {
    		  UpdateTime++;
    		  if (UpdateTime > 9)
    		  {
    			  UpdateTime = 0;
    		  }
    	  }
      }
    };
    
    private int getSize(ItemStack[] items)
    {
    	int size = 0;
    	for (ItemStack i : items)
    	{
    		if (i != null) size++;
    	}
    	return size;
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (command.getName().equalsIgnoreCase("jaildelete"))
		{
			if (sender instanceof Player && !permission((Player) sender, "jail.command.jaildelete", ((Player) sender).isOp())) return false; 
			if (args.length < 1)
				Jail.Message("Usage: /jaildelete [Name]",sender);
			else if (!Jail.zones.containsKey(args[0].toLowerCase()))
			{
				Jail.Message("There is no such jail!", sender);
				return true;
			}
			else
			{
				if (Jail.zones.size() < 2 && Jail.prisoners.size() > 0)
				{
					Jail.Message("You cannot delete last jail zone! Please empty it first (release all players)!",sender);
					return true;
				}
				else
				{

					JailZone zone = Jail.zones.get(args[0].toLowerCase());
					zone.delete();
					Jail.Message("Jail deleted", sender);
					return true;
				}
			}
		}
		
		if (command.getName().equalsIgnoreCase("jailcreatecells"))
		{
			if (!(sender instanceof Player) || !permission((Player) sender, "jail.command.jailcreatecells", ((Player) sender).isOp())) return false; 
			
			if (args.length < 1)
				Jail.Message("Usage: /jailcreatecells [Jail name]",sender);
			else if (!zones.containsKey(args[0]))
				Jail.Message("There is no such jail!", sender);
			else
				JailCellCreation.selectstart((Player) sender, args[0].toLowerCase());
			return true;
		}

		
		if (command.getName().equalsIgnoreCase("jailcreate"))
		{
			if (!(sender instanceof Player) || !permission((Player) sender, "jail.command.jailcreate", ((Player) sender).isOp())) return false; 
			
			if (args.length < 1)
				Jail.Message("Usage: /jailcreate [Name]",sender);
			else
				JailZoneCreation.selectstart((Player) sender, args[0].toLowerCase());
			return true;
		}
		
		if (command.getName().equalsIgnoreCase("jailtelein"))
		{
			if (!(sender instanceof Player) || !permission((Player) sender, "jail.command.jailtelein", ((Player) sender).isOp())) return false; 
			
			if (args.length < 1)
				Jail.Message("Usage: /jaildelete [Name]",sender);
			else if (!Jail.zones.containsKey(args[0].toLowerCase()))
			{
				Jail.Message("There is no such jail!", sender);
			}
			else
			{
				JailZone jail = Jail.zones.get(args[0]);
				((Player) sender).teleport(jail.getTeleportLocation());
			}
			return true;

		}
		
		if (command.getName().equalsIgnoreCase("jailteleout"))
		{
			if (!(sender instanceof Player) || !permission((Player) sender, "jail.command.jailteleout", ((Player) sender).isOp())) return false; 
			
			if (args.length < 1)
				Jail.Message("Usage: /jaildelete [Name]",sender);
			else if (!Jail.zones.containsKey(args[0].toLowerCase()))
			{
				Jail.Message("There is no such jail!", sender);
			}
			else
			{
				JailZone jail = Jail.zones.get(args[0]);
				((Player) sender).teleport(jail.getReleaseTeleportLocation());
			}
			return true;

		}

		
		else if (command.getName().equalsIgnoreCase("jail"))
		{
			if (sender instanceof Player && !permission((Player) sender, "jail.command.jail", ((Player) sender).isOp())) return false; 
			
			PrepareJail(sender, args);
			return true;
			
		}
		else if (command.getName().equalsIgnoreCase("unjail"))
		{
			if (sender instanceof Player && !permission((Player) sender, "jail.command.unjail", ((Player) sender).isOp())) return false; 
			
			if (args.length < 1)
			{
				Jail.Message("Usage: /unjail [Name]", sender);
				return true;
			}
			if (!Jail.prisoners.containsKey(args[0].toLowerCase()))
			{
				Jail.Message("That player is not jailed!", sender);
				return true;
			}
			String playername = args[0].toLowerCase();
			JailPrisoner prisoner = Jail.prisoners.get(playername);
			
			prisoner.release();
			
			if (getServer().getPlayer(prisoner.getName()) == null)
				Jail.Message("Player is offline. He will be automatically released when he connnects.", sender);
			else
				Jail.Message("Player released", sender);
			return true;

		}
		else if (command.getName().equalsIgnoreCase("unjailforce"))
		{
			if (sender instanceof Player && !permission((Player) sender, "jail.command.unjailforce", ((Player) sender).isOp())) return false; 
			
			if (args.length < 1)
			{
				Jail.Message("Usage: /unjailforce [Name]", sender);
				return true;
			}
			if (!Jail.prisoners.containsKey(args[0].toLowerCase()))
			{
				Jail.Message("That player is not jailed!", sender);
				return true;
			}
			String playername = args[0].toLowerCase();
			Jail.prisoners.get(playername).delete();
			Jail.Message("Player deleted from the jail database!", sender);
			return true;

		}
		else if (command.getName().equalsIgnoreCase("jailclear"))
		{
			if (sender instanceof Player && !permission((Player) sender, "jail.command.jailclear", ((Player) sender).isOp())) return false; 
			Object[] names = Jail.prisoners.keySet().toArray();
			for (Object p : names)
			{
				JailPrisoner prisoner = Jail.prisoners.get((String) p);
				String playername = prisoner.getName();
				Player player = null;
				for (Player i : getServer().getOnlinePlayers())
				{
					if (i.getName().equalsIgnoreCase(playername))
						player = i;
				}
					if (player == null)
				{
					
					prisoner.setOfflinePending(true);
					prisoner.setRemainingTime(0);
					InputOutput.UpdatePrisoner(prisoner);
					Jail.prisoners.put(prisoner.getName(), prisoner);					
				}
				else
				{
					UnJail(prisoner, player);
					
				}
			}
			return true;

		}
		else if (command.getName().equalsIgnoreCase("jailclearforce"))
		{
			if (sender instanceof Player && !permission((Player) sender, "jail.command.jailclearforce", ((Player) sender).isOp())) return false; 
			Object[] names = Jail.prisoners.keySet().toArray();
			for (Object p : names)
			{
				Jail.prisoners.get(p).delete();
			}
			Jail.Message("Everyone have been cleared!", sender);
			return true;

		}
		else if (command.getName().equalsIgnoreCase("jailtransfer"))
		{
			if (sender instanceof Player && !permission((Player) sender, "jail.command.jailtransfer", ((Player) sender).isOp())) return false; 
						
			if (args.length < 1)
			{
				Jail.Message("Usage: /jailtransfer [Player Name] (New Jail Name)", sender);
				return true;
			}
			if (!Jail.prisoners.containsKey(args[0].toLowerCase()))
			{
				Jail.Message("That player is not jailed!", sender);
				return true;
			}
			if (args.length > 1 && !Jail.zones.containsKey(args[1].toLowerCase()))
			{
				Jail.Message("Target jail does not exist!", sender);
				return true;
			}
			String playername = args[0].toLowerCase();
			String newjail;
			if (args.length < 2 || args[1].equals(Settings.NearestJailCode)) 
				newjail = null;
			else
				newjail = args[1].toLowerCase();
			JailPrisoner prisoner = Jail.prisoners.get(playername);
			prisoner.transfer(newjail);

			if (getServer().getPlayer(playername) == null)
			{
				
				Jail.Message("Player is offline. He will be automatically transfered when he connnects.", sender);
				
			}
			else
			{
				Jail.Message("Player transfered.", sender);
				
			}

				return true;

		}
		
		else if (command.getName().equalsIgnoreCase("jailtransferall"))
		{
			if (sender instanceof Player && !!permission((Player) sender, "jail.command.jailtransferall", ((Player) sender).isOp())) return false; 
			
			if (args.length < 1)
			{
				Jail.Message("Usage: /jailtransferall [Old Jail Name] (New Jail Name)", sender);
				return true;
			}
			if (!Jail.zones.containsKey(args[0].toLowerCase()))
			{
				Jail.Message("There is no such jail!", sender);
				return true;
			}
			if (args.length > 1 && !Jail.zones.containsKey(args[1].toLowerCase()))
			{
				Jail.Message("Target jail does not exist!", sender);
				return true;
			}
			if (args.length > 1 && args[1].equals(Settings.NearestJailCode))
				PrepareTransferAll(Jail.zones.get(args[0].toLowerCase()), args[1].toLowerCase());
			else
				PrepareTransferAll(Jail.zones.get(args[0].toLowerCase()));
			Jail.Message("Transfer command sent!", sender);
			return true;
		}
		
		else if (command.getName().equalsIgnoreCase("jailstatus"))
		{
			if (!(sender instanceof Player) || !permission((Player) sender, "jail.usercmd.jailstatus", true)) return true; 
			Player player = (Player) sender;
			String message = "";
			JailPrisoner prisoner = Jail.prisoners.get(player.getName().toLowerCase());
			if (!Jail.prisoners.containsKey(player.getName().toLowerCase()))
			{
				Jail.Message("§aYou are not jailed!", player);
				return true;
			}
			else if (prisoner.getRemainingTime() < 0)
			{
				message += ("§cYou are jailed forever! (or until admin releases you)");
			}
			else if (prisoner.getRemainingTime() != 0)
			{
				double time = prisoner.getRemainingTimeMinutes();
				String tim;
				if (time >= 1.0 || time < 0.0)
					tim = String.valueOf((int) Math.round( time ) * 1);
				else
					tim = String.valueOf(Math.round( time * 10.0d ) / 10.0d);
				
				message += ("§cYou are jailed for " + tim + " minutes");
			}
			
			if (prisoner.getReason() != null && !prisoner.getReason().trim().equals(""))
			{
				message += " because " + prisoner.getReason();
			}
			
			message += " by " + prisoner.getJailer();
			Message(message, player);
			return true;
		}
		else if (command.getName().equalsIgnoreCase("jailcheck"))
		{
			if (sender instanceof Player && !permission((Player) sender, "jail.command.jailcheck", ((Player) sender).isOp())) return false; 
			
			if (args.length < 1)
			{
			
				String message = "Jailed players: ";
				if (Jail.prisoners.size() == 0)
				{
					message+= "Nobody is jailed!";
				}
				else
				{
					for (JailPrisoner p : Jail.prisoners.values())
					{
						String time;
						if (p.getRemainingTime() >= 0)
						{
							double timed = p.getRemainingTimeMinutes();
							String tim;
							if (timed >= 1.0 || timed < 0.0)
								tim = String.valueOf((int) Math.round( timed ) * 1);
							else
								tim = String.valueOf(Math.round( timed * 10.0d ) / 10.0d);
							
							time = tim + "min";
						}
						else
						{
							time = "forever";
						}
					message+= p.getName() + "(" + time + ") ";	
					}
				}
				Message(message, sender);
			}
			else
			{
	
				String name = args[0].toLowerCase();
				JailPrisoner prisoner = Jail.prisoners.get(name);
				String message ="";
				if (!Jail.prisoners.containsKey(name))
				{
					Jail.Message("§aPlayer is not jailed!", sender);
					return true; 
				}
				else if (prisoner.getRemainingTime() < 0)
				{
					message += ("§Player is jailed forever! (or until admin releases him)");
				}

				else if (prisoner.getRemainingTime() != 0)
				{
					double time = prisoner.getRemainingTimeMinutes();
					String tim;
					if (time >= 1.0 || time < 0.0)
						tim = String.valueOf((int) Math.round( time ) * 1);
					else
						tim = String.valueOf(Math.round( time * 10.0d ) / 10.0d);
					
					message += ("§Player is jailed for " + tim + " minutes");
				}
				
				if (prisoner.getReason() != null && !prisoner.getReason().trim().equals(""))
				{
					message += " because " + prisoner.getReason();
				}
				
				message += " by " + prisoner.getJailer();
				Message(message, sender);
			}
			return true;
		}
		else if (command.getName().equalsIgnoreCase("jaillist"))
		{
			if (sender instanceof Player && !permission((Player) sender, "jail.command.jaillist", ((Player) sender).isOp())) return false; 

			String message = "Jail list: ";
			if (Jail.zones.size() == 0)
			{
				message+= "You have no jails!";
			}
			else
			{
				for (JailZone z : Jail.zones.values())
				{
				message+= z.getName() + " ";	
				}
			}
			Jail.Message(message, sender);
			return true;
		}
		else if (command.getName().equalsIgnoreCase("jailmute"))
		{
			if (sender instanceof Player && !permission((Player) sender, "jail.command.jailmute", ((Player) sender).isOp())) return false; 
			if (args.length < 1)
			{
				Jail.Message("Usage: /jailmute [Player name]", sender);
				return true;
			}
			JailPrisoner prisoner = prisoners.get(args[0].toLowerCase());
			
			if (prisoner != null && prisoner.isMuted())
			{
				prisoner.setMuted(false);
				InputOutput.UpdatePrisoner(prisoner);
				Message(args[0] + " can speak again!", sender);
			}
			else
			{
				prisoner.setMuted(true);
				InputOutput.UpdatePrisoner(prisoner);
				Message(args[0] + " is now muted!", sender);
			}
			return true;
					
		}
		else if (command.getName().equalsIgnoreCase("jailstop"))
		{
			if (!(sender instanceof Player) || !permission((Player) sender, "jail.command.jailstop", ((Player) sender).isOp())) return false; 
			Player player = (Player) sender;
			JailZoneCreation.players.remove(player.getName());
			JailCellCreation.players.remove(player.getName());
			JailSetManager.players.remove(player.getName());
			
			Message("Any creation stopped", player);
			return true;
		}
		else if (command.getName().equalsIgnoreCase("jailset"))
		{
			if (sender instanceof Player && !permission((Player) sender, "jail.command.jailset", ((Player) sender).isOp())) return false; 
			
			JailSetManager.JailSet(sender, args);
			return true;
		}
		else if (command.getName().equalsIgnoreCase("jailpay"))
		{
			if (!Settings.EnablePaying) return false;
			if (!(sender instanceof Player) || !permission((Player) sender, "jail.command.jailpay", ((Player) sender).isOp())) return false; 
			if (args.length < 1)
			{
				JailPrisoner prisoner = prisoners.get(((Player) sender).getName().toLowerCase());
				if (prisoner == null) 
				{
					Message("You are not jailed!", sender);
					return true;
				}
				if (Settings.PriceForInfiniteJail > 0  && prisoner.getRemainingTime() < 0)
					Message("To get out of this mess, you will have to pay " + Settings.PriceForInfiniteJail + " " + Settings.iConomyMoneyName +".", sender);
				else if (prisoner.getRemainingTime() < 0 || Settings.PricePerMinute == 1)
					Message("Sorry, money won't help you this time.", sender);
				else
				{
					String message = "1 minute of your sentence will cost you " + Settings.PricePerMinute + " " + Settings.iConomyMoneyName +". ";
					message += "That means that cost for releasing you out of the jail is " + Settings.PricePerMinute * Math.round(prisoner.getRemainingTimeMinutes()) + " " + Settings.iConomyMoneyName +".";
					Message(message, sender);
				}
			}
			else
			{
				JailPrisoner prisoner;
				if (args.length > 1)
				{
					prisoner = prisoners.get(args[1].toLowerCase());
					
					if (prisoner == null)
					{
						Message("This player is not jailed!", sender);
						return true;
					}	
				}
				else
				{
					prisoner = prisoners.get(((Player) sender).getName().toLowerCase());
					if (prisoner == null) 
					{
						Message("You are not jailed!", sender);
						return true;
					}
				}
				
				if ((prisoner.getRemainingTime() < 0 && Settings.PriceForInfiniteJail == 0) || (prisoner.getRemainingTime() > 0 && Settings.PricePerMinute == 0))
				{
					if (args.length > 1)
						Message("Sorry, money won't help him this time.", sender);
					else
						Message("Sorry, money won't help you this time.", sender);
					return true;
				}
				
				iConomy iConomy = null;
				Plugin iCoplugin = getServer().getPluginManager().getPlugin("iConomy");
				
				if (iCoplugin == null)
				{
					Message("iConomy error! Please contact the administrator of your server.", sender);
					return true;
				}
				
				iConomy = (iConomy) iCoplugin;

			}
		}
		return false;
    }
    

}
