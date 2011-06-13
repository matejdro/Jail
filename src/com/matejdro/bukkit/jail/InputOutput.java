package com.matejdro.bukkit.jail;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Location;
public class InputOutput {
    private static Connection connection;
    private Jail plugin;
    private PropertiesFile pf;
    
    public static HashMap<Integer, String[]> jailStickParameters = new HashMap<Integer, String[]>();
    
	public InputOutput(Jail instance)
	{
		plugin = instance;
		if (!new File("plugins" + File.separator + "Jail").exists()) {
			try {
			(new File("plugins" + File.separator + "Jail")).mkdir();
			} catch (Exception e) {
			Jail.log.log(Level.SEVERE, "[Jail]: Unable to create plugins/Jail/ directory");
			}
			}
		pf = new PropertiesFile(new File("plugins" + File.separator + "Jail","Jail.properties")); 
		connection = null;
	}
    
    public static synchronized Connection getConnection() {
    	if (connection == null) connection = createConnection();
    	return connection;
    }

    private static Connection createConnection() {
        try {
            if (Settings.UseMySql) {
                Class.forName("com.mysql.jdbc.Driver");
                Connection ret = DriverManager.getConnection(Settings.MySqlConn, Settings.MySqlUsername, Settings.MySqlPassword);
                ret.setAutoCommit(false);
                return ret;
            } else {
                Class.forName("org.sqlite.JDBC");
                Connection ret = DriverManager.getConnection("jdbc:sqlite:plugins" + File.separator + "Jail" + File.separator + "jail.sqlite");
                ret.setAutoCommit(false);
                return ret;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
   public static synchronized void freeConnection() {
        if(connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void LoadSettings()
	{
    	Settings.SelectionTool = pf.getInt("SelectionTool", 268, "What tool is used to create jail zones. Default: Wooden sword.");
    	Settings.BlockDestroyProtection = pf.getBoolean("BlockDestroyProtection", true, "Should jail be protected against destroying it?");
    	Settings.BlockDestroyPenalty = pf.getInt("BlockDestroyPenalty", 15, "For how much time is prisoner's time increased if he tries to destroy jail. Use 0 to disable penalty.");
    	Settings.BlockPlaceProtection = pf.getBoolean("BlockPlaceProtection", true, "Should jail be protected against placing blocks in it it?");
    	Settings.BlockPlacePenalty = pf.getInt("BlockPlacePenalty", 10, "For how much time is prisoner's time increased if he tries place block inside jail. Use 0 to disable penalty.");
    	Settings.PlayerMoveProtection = pf.getBoolean("PlayerMoveProtection", true, "Should we protect prisoners against moving out of jail?");
    	Settings.PlayerMovePenalty = pf.getInt("PlayerMovePenalty", 30, "For how much time is prisoner's time increased if he tries to move out of jail. Use 0 to disable penalty.");
    	Settings.PlayerMoveProtectionAction = pf.getString("PlayerMoveProtectionAction", "guards", "What should happen when player moves out of jail? guards - spawn some guards that will attempt to kill player, teleport - keep player inside by teleporting, escape - announce esacpe and delete player from jail");
    	Settings.FireProtection = pf.getBoolean("FireProtection", true, "Should we protect prisoners against starting fires?");
    	Settings.FirePenalty = pf.getInt("FirePenalty", 10, "For how much time is prisoner's time increased if he tries to start a fire? Use 0 to disable penalty.");
    	Settings.BucketProtection = pf.getBoolean("BucketProtection", true, "Should we protect prisoners against using buckets?");
    	Settings.BucketPenalty = pf.getInt("BucketPenalty", 10, "For how much time is prisoner's time increased if he tries to use a bucket? Use 0 to disable penalty.");
    	Settings.DeleteInventoryOnJail = pf.getBoolean("DeleteInventoryOnJail", false, "Should we delete player's inventory when jailing?");
    	Settings.AutomaticMute = pf.getBoolean("AutomaticMute", false, "Should prisoners be automatically muted after jailing?");
    	Settings.NearestJailCode = pf.getString("NearestJailCode", "nearest", "If you enter this as a jail name, it will automatically search for nearest jail.");
    	Settings.StoreInventory = pf.getBoolean("StoreInventory", true, "Should we take player's inventory when he get jailed and return it to him when he gets released?");
    	Settings.SignText = pf.getString("SignText", "<Player>[NEWLINE]<Time> minutes[NEWLINE]for[NEWLINE]<Reason>", "Text that appears on the cell sign.");
    	Settings.PreventCommands = pf.getString("PreventCommands", "/spawn,/kill,/warp", "Which commands should we prevent from using in jail?").split(",");
    	Settings.CommandPenalty = pf.getInt("CommandPenalty", 10, "For how much time is prisoner's time increased if he tries to use forbidden command. Use 0 to disable penalty.");
    	Settings.EnableJailStick = pf.getBoolean("EnableJailStick", false, "Should we enable jailing with an item?");
    	Settings.JailStickParameters = pf.getString("JailStickParameters", "280,5,10,,police;50,5,20,,admin", "Parameters for JailStick feature. Form: item id,range,time,jail name,reason. You may create multiple entries and split them with semicolon (;). You may leave jail name or reason blank.");
    	Settings.EnableEscape = pf.getBoolean("EnableEscape", false, "When player moves out of the jail, he will be released instead of teleported back.");
    	Settings.CanPrisonerOpenHisChest = pf.getBoolean("CanPrisonerOpenHisChest", false, "Can prisoner open hi own chest? Useful for example in RPG, so prisoners can grab their stuff while escaping.");
    	
    	Settings.GuardHealth = pf.getInt("GuardHealth", 20, "Health of a guard wolf in range 1-20. 1 health unit means half hearth (so 10 health units means 5 hearths of health)");
    	Settings.GuardDamage = pf.getInt("GuardDamage", 1, "How much damage will guard deal per attack? 1 health unit means half hearth (so 10 health units means 5 hearths of damage)");
    	Settings.GuardArmor = pf.getInt("GuardArmor", 0, "20 units of health not enough? Give wolf some armor then. Armor will absorb specified percentage of damage taken. Range: 0-100");
    	Settings.NumberOfGuards = pf.getInt("NumberOfGuards", 3, "How many guards are spawned after player tries to escape?");
    	Settings.Guardinvincibility = pf.getBoolean("Guardinvincibility", false, "Armor not enough? Turn on invincibility! Invincibility will remove any damage, making wolf invulnerable.");
    	Settings.GuardAttackSpeedPercent = pf.getInt("GuardAttackSpeedPercent", 50, "1 unit of damage still too much? Lower the attack speed then. This sets percentage of wolf attack speed, so 100 means default wolf attack speed.");
    	Settings.RespawnGuards = pf.getBoolean("RespawnGuards", true, "true - wolves will respawn immediatelly after being killed, thus making prisoners impossible to kill all wolfs. false - when all wolves that are protecting specified prisoner are killed, prisoner is free");
    	Settings.GuardTeleportDistance = pf.getInt("GuardTeleportDistance", 10, "How many blocks away must prisoner be from the guard to make guard teleport to prisoner. Enter 0 to disable this.");
    	
		Settings.UseMySql = pf.getBoolean("UseMySQL", false, "true = use MySQL database / false = use SQLLite");
		Settings.MySqlConn = pf.getString("MySQLConn", "jdbc:mysql://localhost:3306/minecraft", "MySQL Connection string (only if using MySQL)");
		Settings.MySqlUsername = pf.getString("MySQLUsername", "root", "MySQL Username (only if using MySQL)");
		Settings.MySqlPassword = pf.getString("MySQLPassword", "password", "MySQL Password (only if using MySQL)");
		
		Settings.MessageJail = pf.getString("MessageJail", "§cYou have been jailed!", "");
		Settings.MessageJailReason = pf.getString("MessageJailReason", "§cYou have been jailed! Reason: <Reason>", "");
		Settings.MessageUnjail = pf.getString("MessageUnJail", "§2You have been released! Please respect server rules.", "");
		Settings.MessageDestroyNoPenalty = pf.getString("MessageDestroyNoPenalty", "§cDo not destroy The Jail!", "");
		Settings.MessageDestroyPenalty = pf.getString("MessageDestroyPenalty", "§cDo not destroy The Jail! You have just earned additional 15 minutes in jail!", "");
		Settings.MessageMoveNoPenalty = pf.getString("MessageMoveNoPenalty", "§cDo not try to escape out of Jail!", "");
		Settings.MessageMovePenalty = pf.getString("MessageMovePenalty", "§cDo not try to escape out of Jail! You have just earned additional 30 minutes in jail!!", "");
		Settings.MessagePlaceNoPenalty = pf.getString("MessagePlaceNoPenalty", "§cDo not place blocks inside Jail!", "");
		Settings.MessagePlacePenalty = pf.getString("MessagePlacePenalty", "§cDo not place blocks inside Jail! You have just earned additional 10 minutes in jail!", "");
		Settings.MessageCommandNoPenalty = pf.getString("MessageCommandNoPenalty", "§cDo not try to escape with commands!", "");
		Settings.MessageCommandPenalty = pf.getString("MessageCommandPenalty", "§cDo not try to escape with commands! You have just earned additional 10 minutes in jail!", "");
		Settings.MessageTransfer = pf.getString("MessageTransfer", "§9You have been transferred to another jail!", "");
		Settings.MessageFireNoPenalty = pf.getString("MessageFireNoPenalty", "§cDo not try to burn the jail!", "");
		Settings.MessageFirePenalty = pf.getString("MessageFirePenalty", "§cDo not try to burn the jail! You have just earned additional 15 minutes in jail!", "");
		Settings.MessageBucketNoPenalty = pf.getString("MessageBucketNoPenalty", "§cDo not try to flood the jail!", "");
		Settings.MessageBucketPenalty = pf.getString("MessageBucketPenalty", "§cDo not try to flood the jail! You have just earned additional 10 minutes in jail!", "");
		Settings.MessageMute = pf.getString("MessageMute", "Stop chatting and quietly wait for the end of your sentence!", "");
		Settings.AlwaysTeleportIntoJailCenter = pf.getBoolean("AlwaysTeleportIntoJailCenter", false, "When player tries to escape, should he always be teleported back to teleport point? Otherwise, he will simply be kept inside jail?");
		
		loadJailStickParameters();
		
		pf.save();
	}
    
    public void loadJailStickParameters()
    {
    	for (String i : Settings.JailStickParameters.split(";"))
    	{
    		jailStickParameters.put(Integer.parseInt(i.substring(0, i.indexOf(","))), i.split(","));
    		Jail.log.info(i.substring(0, i.indexOf(",")));
    	}
    		
    }
    
    public void LoadJails()
    {
    	try {
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet set = null;
			
			conn = getConnection();
			ps = conn.prepareStatement("SELECT * FROM jail_zones");
			set = ps.executeQuery();
			//conn.commit();
			
			while (set.next())
			{
				
				
				String name = set.getString("name").toLowerCase();
				double X1 = set.getDouble("X1");
				double Y1 = set.getDouble("Y1");
				double Z1 = set.getDouble("Z1");
				double X2 = set.getDouble("X2");
				double Y2 = set.getDouble("Y2");
				double Z2 = set.getDouble("Z2");
				double teleX = set.getDouble("teleX");
				double teleY = set.getDouble("teleY");
				double teleZ = set.getDouble("teleZ");
				double freeX = set.getDouble("freeX");
				double freeY = set.getDouble("freeY");
				double freeZ = set.getDouble("freeZ");
				String teleWorld = set.getString("teleWorld");
				String freeWorld = set.getString("freeWorld");
				
				JailZone jail = new JailZone(name, X1, Y1, Z1, X2, Y2, Z2, teleX, teleY, teleZ, freeX, freeY, freeZ, teleWorld, freeWorld);
				
				
				Jail.zones.put(jail.getName(), jail);
			}
			
			set.close();
			ps.close();
			Jail.log.log(Level.INFO,"[Jail] Loaded " + String.valueOf(Jail.zones.size()) + " jail zones.");

		} catch (SQLException e) {
			Jail.log.log(Level.SEVERE, "[Jail] Error while loading Jail zones! - " + e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    public void LoadPrisoners()
    {
    	try {
			Connection conn;
			PreparedStatement ps = null;
			ResultSet set = null;
			
			conn = getConnection();
			ps = conn.prepareStatement("SELECT * FROM jail_prisoners");
			set = ps.executeQuery();
			//conn.commit();
			
			while (set.next())
			{
				
				String name = set.getString("PlayerName").toLowerCase();
				int remaintime = set.getInt("RemainTime");
				String jailname = set.getString("JailName");
				Boolean offline = set.getBoolean("Offline");
				String transferDest = set.getString("TransferDest");
				String reason = set.getString("reason");
				String inventory = set.getString("Inventory");
				String jailer = set.getString("Jailer");
				
				JailPrisoner p = new JailPrisoner(name, remaintime, jailname, offline, transferDest, reason, false,  inventory, jailer);
				
				Jail.prisoners.put(p.getName(), p);
			}
			
			set.close();
			ps.close();
			Jail.log.log(Level.INFO,"[Jail] Loaded " + String.valueOf(Jail.prisoners.size()) + " prisoners.");

		} catch (SQLException e) {
			Jail.log.log(Level.SEVERE, "[Jail] Error while loading prisoners from the database! - " + e.getMessage() );
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void LoadCells()
    {
    	try {
			Connection conn;
			PreparedStatement ps = null;
			ResultSet set = null;
			
			conn = getConnection();
			ps = conn.prepareStatement("SELECT * FROM jail_cells");
			set = ps.executeQuery();
			//conn.commit();
			int count = 0;
			while (set.next())
			{
				count++;
				String jailname = set.getString("JailName");
				String teleport = set.getString("Teleport");
				String sign = set.getString("Sign");
				String chest = set.getString("Chest");
				String secondchest = set.getString("SecondChest");
				String player = set.getString("Player");
				
				JailPrisoner prisoner = Jail.prisoners.get(player);
				if (prisoner == null)
					player = "";
				
				JailCell cell = new JailCell(jailname,  player);
				cell.setChest(chest);
				cell.setSecondChest(secondchest);
				cell.setTeleportLocation(teleport);
				cell.setSign(sign);
				
				cell.getJail().getCellList().add(cell);
				
				if (prisoner != null)
					prisoner.setCell(cell);
			}
			
			set.close();
			ps.close();
			Jail.log.log(Level.INFO,"[Jail] Loaded " + String.valueOf(count) + " cells.");

		} catch (SQLException e) {
			Jail.log.log(Level.SEVERE, "[Jail] Error while loading prisoners from the database! - " + e.getMessage() );
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void InsertZone(JailZone z)
    {
    	try {
    		Location firstCorner = z.getFirstCorner();
    		Location secondCorner = z.getSecondCorner();
    		Location telePoint = z.getTeleportLocation();
    		Location freePoint = z.getReleaseTeleportLocation();
    		
			Connection conn = InputOutput.getConnection();
			PreparedStatement ps = conn.prepareStatement("INSERT INTO jail_zones (name, X1, Y1, Z1, X2, Y2, Z2, teleX, teleY, teleZ, freeX, freeY, FreeZ, teleWorld, freeWorld) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1, z.name.toLowerCase());
			ps.setDouble(2, firstCorner.getX());
			ps.setDouble(3, firstCorner.getY());
			ps.setDouble(4, firstCorner.getZ());
			ps.setDouble(5, secondCorner.getX());
			ps.setDouble(6, secondCorner.getY());
			ps.setDouble(7, secondCorner.getZ());
			ps.setDouble(8, telePoint.getX());
			ps.setDouble(9, telePoint.getY());
			ps.setDouble(10, telePoint.getZ());
			ps.setDouble(11, freePoint.getX());
			ps.setDouble(12, freePoint.getY());
			ps.setDouble(13, freePoint.getZ());
			ps.setString(14, telePoint.getWorld().getName());
			ps.setString(15, freePoint.getWorld().getName());
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			Jail.log.log(Level.SEVERE,"[Jail] Error while creating Jail Zone! - " + e.getMessage() );
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void UpdateZone(JailZone z)
    {
    	try {
    		Location firstCorner = z.getFirstCorner();
    		Location secondCorner = z.getSecondCorner();
    		Location telePoint = z.getTeleportLocation();
    		Location freePoint = z.getReleaseTeleportLocation();
    		
			Connection conn = InputOutput.getConnection();
			PreparedStatement ps = conn.prepareStatement("UPDATE jail_zones SET X1 = ?, Y1 = ?, Z1 = ?, X2 = ?, Y2 = ?, Z2 = ?, teleX = ?, teleY = ?, teleZ = ?, freeX = ?, freeY = ?, FreeZ = ?, teleWorld = ?, freeWorld = ? WHERE name = ?");
			ps.setDouble(1, firstCorner.getX());
			ps.setDouble(2, firstCorner.getY());
			ps.setDouble(3, firstCorner.getZ());
			ps.setDouble(4, secondCorner.getX());
			ps.setDouble(5, secondCorner.getY());
			ps.setDouble(6, secondCorner.getZ());
			ps.setDouble(7, telePoint.getX());
			ps.setDouble(8, telePoint.getY());
			ps.setDouble(9, telePoint.getZ());
			ps.setDouble(10, freePoint.getX());
			ps.setDouble(11, freePoint.getY());
			ps.setDouble(12, freePoint.getZ());
			ps.setString(13, telePoint.getWorld().getName());
			ps.setString(14, freePoint.getWorld().getName());
			ps.setString(15, z.name.toLowerCase());
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			Jail.log.log(Level.SEVERE,"[Jail] Error while creating Jail Zone! - " + e.getMessage() );
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    
    public static void DeleteZone(JailZone z)
    {
    	try {
			Connection conn = InputOutput.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM jail_zones WHERE name = ?");
			ps.setString(1, z.getName());
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			Jail.log.log(Level.SEVERE,"[Jail] Error while deleting Zone from DB! - " + e.getMessage() );
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void InsertPrisoner(JailPrisoner p)
    {
    	try {
			Connection conn = InputOutput.getConnection();
			PreparedStatement ps = conn.prepareStatement("INSERT INTO jail_prisoners  (PlayerName, RemainTime, JailName, Offline, TransferDest, reason, muted, Inventory,Jailer) VALUES (?,?,?,?,?,?,?,?,?)");
			ps.setString(1, p.getName());
			ps.setInt(2, p.getRemainingTime());
			if (p.getJail() == null)
			{
				ps.setString(3, "");
			}
			else
			{
				ps.setString(3, p.getJail().getName());
			}
			ps.setBoolean(4, p.offlinePending());
			ps.setString(5, p.getTransferDestination());
			ps.setString(6, p.getReason());
			ps.setBoolean(7, p.isMuted());
			ps.setString(8, p.getInventory());
			ps.setString(9, p.getJailer());
			ps.executeUpdate();
			conn.commit();

		} catch (SQLException e) {
			Jail.log.log(Level.SEVERE,"[Jail] Error while inserting Prisoner into DB! - " + e.getMessage() );
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void InsertCell(JailCell c)
    {
    	try {
			Connection conn = InputOutput.getConnection();
			PreparedStatement ps = conn.prepareStatement("INSERT INTO jail_cells (JailName, Teleport, Sign, Chest,SecondChest, Player) VALUES (?,?,?,?,?,?)");
			ps.setString(1, c.getJail().getName());
			
			ps.setString(2, String.valueOf(c.getTeleportLocation().getX()) + "," + String.valueOf(c.getTeleportLocation().getY()) + "," + String.valueOf(c.getTeleportLocation().getZ()));
			if (c.getSign() != null)
				ps.setString(3, String.valueOf(c.getSign().getX()) + "," + String.valueOf(c.getSign().getY()) + "," + String.valueOf(c.getSign().getZ()));
			else
				ps.setString(3, "");
			if (c.getChest() != null)
				ps.setString(4, String.valueOf(c.getChest().getX()) + "," + String.valueOf(c.getChest().getY()) + "," + String.valueOf(c.getChest().getZ()));
			else
				ps.setString(4, "");
			if (c.getSecondChest() != null)
				ps.setString(5, String.valueOf(c.getSecondChest().getX()) + "," + String.valueOf(c.getSecondChest().getY()) + "," + String.valueOf(c.getSecondChest().getZ()));
			else
				ps.setString(5, "");

			ps.setString(6, c.getPlayerName());
			ps.executeUpdate();
			conn.commit();

		} catch (SQLException e) {
			Jail.log.log(Level.SEVERE,"[Jail] Error while inserting Cell into DB! - " + e.getMessage() );
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void UpdateCell(JailCell c)
    {
    	try {
			Connection conn = InputOutput.getConnection();
			if (conn == null || conn.isClosed()) return;
			PreparedStatement ps = conn.prepareStatement("UPDATE jail_cells SET Player = ? WHERE Teleport = ?");
			if (c.getPlayerName() == null)
			{
				ps.setString(1, "");
			}
			else
			{
				ps.setString(1, c.getPlayerName());
			}
			ps.setString(2, String.valueOf(c.getTeleportLocation().getX()) + "," + String.valueOf(c.getTeleportLocation().getY()) + "," + String.valueOf(c.getTeleportLocation().getZ()));
			
			ps.executeUpdate();
			conn.commit();

		} catch (SQLException e) {
			Jail.log.log(Level.SEVERE,"[Jail] Error while updating Cell into DB!");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public static void DeleteCell(JailCell c)
    {
    	try {
			Connection conn = InputOutput.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM jail_cells WHERE Teleport = ?");
			ps.setString(1, String.valueOf(c.getTeleportLocation().getX()) + "," + String.valueOf(c.getTeleportLocation().getY()) + "," + String.valueOf(c.getTeleportLocation().getZ()));
			ps.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			Jail.log.log(Level.SEVERE,"[Jail] Error while deleting Cell from DB! - " + e.getMessage() );
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


    
    public static void UpdatePrisoner(JailPrisoner p)
    {
    	try {
			Connection conn = InputOutput.getConnection();
			if (conn == null || conn.isClosed()) return;
			PreparedStatement ps = conn.prepareStatement("UPDATE jail_prisoners SET RemainTime = ?, JailName = ?, Offline = ?, TransferDest = ?, muted = ?, Inventory = ? WHERE PlayerName = ?");
			ps.setInt(1, Math.round(p.getRemainingTime()));
			if (p.getJail() == null)
			{
				ps.setString(2, "");
			}
			else
			{
				ps.setString(2, p.getJail().getName());
			}
			ps.setBoolean(3, p.offlinePending());
			ps.setString(4, p.getTransferDestination());
			ps.setBoolean(5, p.isMuted());
			ps.setString(6, p.getInventory());

			ps.setString(7, p.getName());
			ps.executeUpdate();
			conn.commit();

		} catch (SQLException e) {
			Jail.log.log(Level.SEVERE,"[Jail] Error while updating Prisoner into DB!");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void DeletePrisoner(JailPrisoner p)
    {
    	try {
			Connection conn = InputOutput.getConnection();
			PreparedStatement ps = conn.prepareStatement("DELETE FROM jail_prisoners WHERE PlayerName = ?");
			ps.setString(1, p.getName());
			ps.executeUpdate();
			conn.commit();

		} catch (SQLException e) {
			Jail.log.log(Level.SEVERE,"[Jail] Error while deleting Prisoner from DB! - " + e.getMessage() );
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void UpdatePrisoners()
    {
    	if (Jail.prisoners.size() == 0) return;
    	try {
			Connection conn = InputOutput.getConnection();
			if (conn == null || conn.isClosed()) return;
			PreparedStatement ps = conn.prepareStatement("UPDATE jail_prisoners SET RemainTime = ?, JailName = ?, Offline = ?, TransferDest = ?, muted = ?, Inventory = ? WHERE PlayerName = ?");
			for (JailPrisoner p : Jail.prisoners.values())
			{
				ps.setInt(1, p.getRemainingTime());
				ps.setString(2, p.getJail().getName());
				ps.setBoolean(3, p.offlinePending());
				ps.setString(4, p.getTransferDestination());
				ps.setBoolean(5, p.isMuted());
				ps.setString(6, p.getInventory());

				ps.setString(7, p.getName());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();

		} catch (SQLException e) {
			Jail.log.log(Level.SEVERE,"[Jail] Error while updating Prisoner into DB! - " + e.getMessage() );
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
        
    


    
    public void PrepareDB()
    {
    	Connection conn;
        Statement st = null;
        try {
            conn = InputOutput.getConnection();//            {
            	st = conn.createStatement();
            	if (Settings.UseMySql)
                {
                	st.executeUpdate("CREATE TABLE IF NOT EXISTS `jail_prisoners` ( `PlayerName` varchar(250) NOT NULL, `RemainTime` int(11) DEFAULT NULL, `JailName` varchar(250) DEFAULT NULL, `Offline` varchar(250) DEFAULT NULL, `TransferDest` varchar(250) DEFAULT NULL , `reason` varchar(250) DEFAULT NULL, `muted` boolean DEFAULT false, Inventory TEXT DEFAULT NULL, Jailer VARCHAR(250) DEFAULT NULL, PRIMARY KEY (`PlayerName`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
                	st.executeUpdate("CREATE TABLE IF NOT EXISTS `jail_zones` ( `name` varchar(250) NOT NULL DEFAULT '', `X1` double DEFAULT NULL, `Y1` double DEFAULT NULL, `Z1` double DEFAULT NULL, `X2` double DEFAULT NULL, `Y2` double DEFAULT NULL, `Z2` double DEFAULT NULL, `teleX` double DEFAULT NULL, `teleY` double DEFAULT NULL, `teleZ` double DEFAULT NULL, `freeX` double DEFAULT NULL, `freeY` double DEFAULT NULL, `FreeZ` double DEFAULT NULL, `teleWorld` varchar(250) DEFAULT NULL, `freeWorld` varchar(250) DEFAULT NULL , PRIMARY KEY (`name`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
                	st.executeUpdate("CREATE TABLE IF NOT EXISTS `jail_cells` ( `JailName` varchar(250) NOT NULL, `Teleport` varchar(250) NOT NULL, `Sign` varchar(250) DEFAULT NULL , `Chest` varchar(250) DEFAULT NULL, `SecondChest` varchar(250) DEFAULT NULL, Player varchar(250) DEFAULT NULL, PRIMARY KEY (Teleport) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
                }
                else
                {
                	st.executeUpdate("CREATE TABLE IF NOT EXISTS \"jail_prisoners\" (\"PlayerName\" VARCHAR PRIMARY KEY  NOT NULL , \"RemainTime\" INTEGER, \"JailName\" VARCHAR, \"Offline\" BOOLEAN, \"TransferDest\" VARCHAR, `reason` VARCHAR, `muted` BOOLEAN, Inventory STRING, Jailer VARCHAR)");
                    st.executeUpdate("CREATE TABLE IF NOT EXISTS \"jail_zones\" (\"name\" VARCHAR PRIMARY KEY  NOT NULL , \"X1\" DOUBLE, \"Y1\" DOUBLE, \"Z1\" DOUBLE, \"X2\" DOUBLE, \"Y2\" DOUBLE, \"Z2\" DOUBLE, \"teleX\" DOUBLE, \"teleY\" DOUBLE, \"teleZ\" DOUBLE, \"freeX\" DOUBLE, \"freeY\" DOUBLE, \"FreeZ\" DOUBLE, \"teleWorld\" VARCHAR, \"freeWorld\" STRING)");
                    st.executeUpdate("CREATE TABLE IF NOT EXISTS `jail_cells` ( `JailName` VARCHAR NOT NULL,  `Teleport` VARCHAR  PRIMARY_KEY NOT NULL, `Sign` VARCHAR DEFAULT NULL , `Chest` VARCHAR DEFAULT NULL, `SecondChest` VARCHAR DEFAULT NULL, Player VARCHAR DEFAULT NULL);");
                }
                conn.commit();
                st.close();


//            }
        } catch (SQLException e) {
            Jail.log.log(Level.SEVERE, "[Jail]: Error while creating tables! - " + e.getMessage());
            e.printStackTrace();
    }
        UpdateDB();
    }
    
    public void UpdateDB()
    {
    	Update("SELECT reason,muted FROM jail_prisoners", "ALTER TABLE jail_prisoners ADD reason VARCHAR;ALTER TABLE jail_prisoners ADD muted BOOLEAN", "ALTER TABLE jail_prisoners ADD reason varchar(250);ALTER TABLE jail_prisoners ADD muted boolean" ); //Jail reason & mute update - 0.6
    	Update("SELECT Inventory FROM jail_prisoners", "ALTER TABLE jail_prisoners ADD Inventory VARCHAR;", "ALTER TABLE jail_prisoners ADD Inventory varchar(250);" ); //Jail inventory storage - 0.7
    	Update("SELECT Jailer FROM jail_prisoners", "ALTER TABLE jail_prisoners ADD Jailer VARCHAR;", "ALTER TABLE jail_prisoners ADD Jailer varchar(250);" ); //Jailer log - 0.7   
    	UpdateType("jail_prisoners", "Inventory", "TEXT");
    }
    
    public void Update(String check, String sql)
    {
    	Update(check, sql, sql);
    }
    
    public void Update(String check, String sqlite, String mysql)
    {
    	try
    	{
    		Statement statement = getConnection().createStatement();
			statement.executeQuery(check);
			statement.close();
    	}
    	catch(SQLException ex)
    	{
    		Jail.log.log(Level.INFO, "[Jail] Updating database");
    		try {
    			String[] query;
    			if (Settings.UseMySql)
    				query = mysql.split(";");
    			else
    				query = sqlite.split(";");
            	Connection conn = getConnection();
    			Statement st = conn.createStatement();
    			for (String q : query)	
    				st.executeUpdate(q);
    			conn.commit();
    			st.close();
    		} catch (SQLException e) {
    			Jail.log.log(Level.SEVERE, "[Jail] Error while updating tables to the new version - " + e.getMessage());
                e.printStackTrace();
    	}
        
	}
    	
	
    	
    	
    	
    }
    
    public void UpdateType(String table, String field, String type)
    {
    	try
    	{
    		if (!Settings.UseMySql) return;
    		Connection conn = getConnection();
    		DatabaseMetaData meta = conn.getMetaData();
    	    ResultSet rsColumns = null;
    	    
    	    rsColumns = meta.getColumns(null, null, table, null);
    	    while (rsColumns.next()) {
    	      String columnName = rsColumns.getString("COLUMN_NAME");
    	      String columnType = rsColumns.getString("TYPE_NAME");
    	      if (columnName.equals(field) && !columnType.equals(type))
    	      {
    	    	  Jail.log.log(Level.INFO, "[Jail] Updating database");
    	    	  Statement st = conn.createStatement();
    	    	  st.executeUpdate("ALTER TABLE " + table + " MODIFY " + field + " " + type + "; ");
    	    	  conn.commit();
    	    	  st.close();
    	    	  break;
    	      }
    	    }
    	    
    	    rsColumns.close();
    	}
    	catch(SQLException ex)
    	{
    			Jail.log.log(Level.SEVERE, "[Jail] Error while updating tables to the new version - " + ex.getMessage());
                ex.printStackTrace();
    	}
        
	}

}
