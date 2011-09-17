package com.matejdro.bukkit.jail;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.Timer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.matejdro.bukkit.jail.commands.BaseCommand;
import com.matejdro.bukkit.jail.commands.JailCheckCommand;
import com.matejdro.bukkit.jail.commands.JailClearCommand;
import com.matejdro.bukkit.jail.commands.JailClearForceCommand;
import com.matejdro.bukkit.jail.commands.JailCommand;
import com.matejdro.bukkit.jail.commands.JailCreateCellsCommand;
import com.matejdro.bukkit.jail.commands.JailCreateCommand;
import com.matejdro.bukkit.jail.commands.JailCreateWeCommand;
import com.matejdro.bukkit.jail.commands.JailDeleteCellCommand;
import com.matejdro.bukkit.jail.commands.JailDeleteCellsCommand;
import com.matejdro.bukkit.jail.commands.JailDeleteCommand;
import com.matejdro.bukkit.jail.commands.JailListCellsCommand;
import com.matejdro.bukkit.jail.commands.JailListCommand;
import com.matejdro.bukkit.jail.commands.JailMuteCommand;
import com.matejdro.bukkit.jail.commands.JailPayCommand;
import com.matejdro.bukkit.jail.commands.JailSetCommand;
import com.matejdro.bukkit.jail.commands.JailStatusCommand;
import com.matejdro.bukkit.jail.commands.JailStickCommand;
import com.matejdro.bukkit.jail.commands.JailStopCommand;
import com.matejdro.bukkit.jail.commands.JailTeleInCommand;
import com.matejdro.bukkit.jail.commands.JailTeleOutCommand;
import com.matejdro.bukkit.jail.commands.JailTransferAllCommand;
import com.matejdro.bukkit.jail.commands.JailTransferCommand;
import com.matejdro.bukkit.jail.commands.UnJailCommand;
import com.matejdro.bukkit.jail.commands.UnJailForceCommand;
import com.matejdro.bukkit.jail.listeners.JailBlockListener;
import com.matejdro.bukkit.jail.listeners.JailEntityListener;
import com.matejdro.bukkit.jail.listeners.JailPlayerListener;
import com.matejdro.bukkit.jail.listeners.JailPlayerProtectionListener;
import com.matejdro.bukkit.jail.listeners.JailSpoutListener;

public class Jail extends JavaPlugin {
	public static Logger log = Logger.getLogger("Minecraft");

	private JailPlayerListener PlayerListener;
	private JailBlockListener BlockListener;
	private JailPlayerProtectionListener PlayerPreventListener;
	private JailEntityListener EntityListener;
	private JailSpoutListener SpoutListener;
	public JailAPI API;
	private InputOutput IO;
	public static HashMap<String,JailZone> zones = new HashMap<String,JailZone>();
	public static HashMap<String,JailPrisoner> prisoners = new HashMap<String,JailPrisoner>();
	public static HashMap<Wolf, JailPrisoner> guards = new HashMap<Wolf, JailPrisoner>();
	public static HashMap<Player, Boolean> jailStickToggle = new HashMap<Player, Boolean>();
	private Timer timer;
	private int UpdateTime = 1;
	
	public static Jail instance;
	
	public static Plugin permissions = null;
	
	public static Boolean timeUpdateRunning = false;
	
	private HashMap<String, BaseCommand> commands = new HashMap<String, BaseCommand>();

	//Test
	//public Jail(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		//super(pluginLoader, instance, desc, folder, plugin, cLoader);
		
		
      // }
	

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		timer.stop();
		InputOutput.freeConnection();
		for (Wolf w : guards.keySet())
			w.remove();
	}

	@Override
	public void onEnable() {
		instance = this;
		PlayerListener = new JailPlayerListener(this);
		BlockListener = new JailBlockListener();
		PlayerPreventListener = new JailPlayerProtectionListener(this);
		EntityListener = new JailEntityListener(this);
		IO = new InputOutput();
		API = new JailAPI();
		UpdateTime = 0;
		
		IO.LoadSettings();
		IO.PrepareDB();
		IO.LoadJails();
		IO.LoadPrisoners();
		IO.LoadCells();
		
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, PlayerPreventListener, Event.Priority.Low, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, PlayerListener, Event.Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, PlayerListener, Event.Priority.High, this);
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_BREAK, BlockListener, Event.Priority.Low, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, PlayerPreventListener, Event.Priority.Low, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_TELEPORT, PlayerPreventListener, Event.Priority.Low, this);
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACE, BlockListener, Event.Priority.High, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_RESPAWN, PlayerPreventListener, Event.Priority.Low, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, PlayerListener, Event.Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, EntityListener, Event.Priority.Monitor, this);
		getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, EntityListener, Event.Priority.Low, this);
		getServer().getPluginManager().registerEvent(Event.Type.ENTITY_EXPLODE, EntityListener, Event.Priority.High, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, PlayerListener, Event.Priority.Lowest, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, PlayerPreventListener, Event.Priority.High, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, PlayerPreventListener, Event.Priority.High, this);	
		
		Plugin plugin = Jail.instance.getServer().getPluginManager().getPlugin("Spout");
		if (plugin != null)
		{
			SpoutListener = new JailSpoutListener();
			getServer().getPluginManager().registerEvent(Event.Type.CUSTOM_EVENT, SpoutListener, Event.Priority.Normal, this);	
		}
		
		timer = new Timer(1000,action);
		timer.start();
		
		permissions = this.getServer().getPluginManager().getPlugin("Permissions");
		
		commands.put("jail", new JailCommand());
		commands.put("unjail", new UnJailCommand());
		commands.put("jaildelete", new JailDeleteCommand());
		commands.put("jailcreatecells", new JailCreateCellsCommand());
		commands.put("jailtelein", new JailTeleInCommand());
		commands.put("jailteleout", new JailTeleOutCommand());
		commands.put("unjailforce", new UnJailForceCommand());
		commands.put("jailclear", new JailClearCommand());
		commands.put("jailclearforce", new JailClearForceCommand());
		commands.put("jailtransfer", new JailTransferCommand());
		commands.put("jailtransferall", new JailTransferAllCommand());
		commands.put("jailstatus", new JailStatusCommand());
		commands.put("jailcheck", new JailCheckCommand());
		commands.put("jaillist", new JailListCommand());
		commands.put("jailmute", new JailMuteCommand());
		commands.put("jailstop", new JailStopCommand());
		commands.put("jailset", new JailSetCommand());
		commands.put("jailpay", new JailPayCommand());
		commands.put("jailcreate", new JailCreateCommand());
		commands.put("jaildeletecells", new JailDeleteCellsCommand());
		commands.put("jaillistcells", new JailListCellsCommand());
		commands.put("jailstick", new JailStickCommand());
		commands.put("jailcreatewe", new JailCreateWeCommand());
		commands.put("jaildeletecell", new JailDeleteCellCommand());
		
		log.info("[Jail] " + getDescription().getFullName() + " loaded!");
	}
	
	ActionListener action = new ActionListener ()
    {
      public void actionPerformed (ActionEvent event)
      {
    	  if (Jail.timeUpdateRunning) return; 
    	  if (UpdateTime == 0)
    	  {
    		  Jail.timeUpdateRunning = true;
    		  getServer().getScheduler().scheduleSyncDelayedTask(Jail.instance, new Runnable() {

    			    public void run() {
    			    	
    	    	    	  for (JailPrisoner prisoner : prisoners.values())
    	    	    	  {
    	    	    		  Player player = getServer().getPlayer(prisoner.getName());
    	    	    		  if (prisoner.getRemainingTime() > 0 && (player != null || prisoner.getJail().getSettings().getBoolean(Setting.CountdownTimeWhenOffline)))
    	    	    		  {
    	    	    			  	  prisoner.setRemainingTime(prisoner.getRemainingTime() - 1);
    	    	        			  InputOutput.UpdatePrisoner(prisoner);   				  
    	    	
    	    	    		  }
    	    	    		  else if (player != null && prisoner.getRemainingTime() == 0 && prisoner.offlinePending() == false)
	    	        		  {
  	        					PrisonerManager.UnJail(prisoner, player);
	    	        		  }
    	    	    		  
    	    	    		  if (player != null && prisoner.getJail().getSettings().getDouble(Setting.MaximumAFKTime) > 0.0)
    	    	    		  {
    	    	    			  prisoner.setAFKTime(prisoner.getAFKTime() + 1);
    	    	    			  if (prisoner.getAFKTimeMinutes() > prisoner.getJail().getSettings().getDouble(Setting.MaximumAFKTime))
    	    	    			  {
    	    	    				  prisoner.setAFKTime(0);
    	    	    				  player.kickPlayer(prisoner.getJail().getSettings().getString(Setting.MessageAFKKick));
    	    	    			  }
    	    	    		  }
    			    }
    	    	    	Jail.timeUpdateRunning = false;
    			    }
    			    
    			}, 1L);
        		  
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
    
    

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
    	BaseCommand cmd = commands.get(command.getName().toLowerCase());
    	if (cmd != null) return cmd.execute(sender, args);
    	return false;
    }
    

}
