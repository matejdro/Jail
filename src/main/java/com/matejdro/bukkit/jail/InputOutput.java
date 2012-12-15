package com.matejdro.bukkit.jail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.bukkit.block.Sign;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
public class InputOutput {
    private static Connection connection;
    public static YamlConfiguration global;
    public static YamlConfiguration jails;
    
    public static HashMap<Integer, String[]> jailStickParameters = new HashMap<Integer, String[]>();
    
	public InputOutput()
	{
		if (!Jail.instance.getDataFolder().exists()) {
			try {
			(Jail.instance.getDataFolder()).mkdir();
			} catch (Exception e) {
			Jail.log.log(Level.SEVERE, "[Jail]: Unable to create " + Jail.instance.getDataFolder().getAbsolutePath() + " directory");
			}
			}
		global = new YamlConfiguration();
		jails = new YamlConfiguration(); 
		connection = null;
	}
    
    public static synchronized Connection getConnection() {
    	if (connection == null) connection = createConnection();
    	if(Settings.getGlobalBoolean(Setting.UseMySQL)) {
            try {
                if(!connection.isValid(10)) connection = createConnection();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    	return connection;
    }

    private static Connection createConnection() {
        try {
            if (Settings.getGlobalBoolean(Setting.UseMySQL)) {
                Class.forName("com.mysql.jdbc.Driver");
                Connection ret = DriverManager.getConnection(Settings.getGlobalString(Setting.MySQLConn), Settings.getGlobalString(Setting.MySQLUsername), Settings.getGlobalString(Setting.MySQLPassword));
                ret.setAutoCommit(false);
                return ret;
            } else {
                Class.forName("org.sqlite.JDBC");
                Connection ret = DriverManager.getConnection("jdbc:sqlite:" +  new File(Jail.instance.getDataFolder().getPath(), "jail.sqlite").getPath());
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
		Connection conn = getConnection();
        if(conn != null) {
            try {
            	conn.close();
            	conn = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void LoadSettings()
	{
    	try {
    		if (!new File(Jail.instance.getDataFolder(),"global.yml").exists()) global.save(new File(Jail.instance.getDataFolder(),"global.yml"));
    		if (!new File(Jail.instance.getDataFolder(),"jails.yml").exists()) jails.save(new File(Jail.instance.getDataFolder(),"jails.yml"));

    		global.load(new File(Jail.instance.getDataFolder(),"global.yml"));
	    	jails.load(new File(Jail.instance.getDataFolder(),"jails.yml"));
	    	for (Setting s : Setting.values())
	    	{
	    		if (global.get(s.getString()) == null) global.set(s.getString(), s.getDefault());
	    	}
	    	loadJailStickParameters();
	    	
	    	global.save(new File(Jail.instance.getDataFolder(),"global.yml"));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public void loadJailStickParameters()
    {
    	for (String i : Settings.getGlobalString(Setting.JailStickParameters).split(";"))
    	{
    		jailStickParameters.put(Integer.parseInt(i.substring(0, i.indexOf(","))), i.split(","));
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
			Jail.zones.clear();
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
				if (jails.get(jail.name + ".Protections.EnableBlockDestroyProtection") == null) jails.set(jail.name + ".Protections.EnableBlockDestroyProtection", true);
			}
			
			try {
				jails.save(new File(Jail.instance.getDataFolder(),"jails.yml"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			Jail.prisoners.clear();
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
				String permissions = set.getString("Permissions");
				String previousPosition = set.getString("PreviousPosition");
				Boolean muted = set.getBoolean("muted");
				
				JailPrisoner p = new JailPrisoner(name, remaintime, jailname, null, offline, transferDest, reason, muted,  inventory, jailer, permissions);
				p.setPreviousPosition(previousPosition);
				
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
				String jailname = set.getString("JailName");
				String teleport = set.getString("Teleport");
				String sign = set.getString("Sign");
				String chest = set.getString("Chest");
				String player = set.getString("Player");
				String name = set.getString("Name");
				
				JailPrisoner prisoner = Jail.prisoners.get(player);
				if (prisoner == null)
					player = "";
				
				JailCell cell = new JailCell(jailname,  player, name);
				cell.setTeleportLocation(teleport);

				if (!Jail.zones.containsKey(jailname))
				{
					final JailCell fcell = cell;
					Jail.instance.getServer().getScheduler().scheduleSyncDelayedTask(Jail.instance, new Runnable() {

					    public void run() {
					        fcell.delete();
					    }
					}, 1);
					continue;
				}
				
				cell.setChest(chest);

				for (String s : sign.split(";"))
					cell.addSign(s);
				
				cell.getJail().getCellList().add(cell);
				count++;
						
				if (prisoner != null)
					prisoner.setCell(cell);
			}
			
			set.close();
			ps.close();
			Jail.log.log(Level.INFO,"[Jail] Loaded " + String.valueOf(count) + " cells.");

		} catch (SQLException e) {
			Jail.log.log(Level.SEVERE, "[Jail] Error while loading prisoners from the database! - " + e.getMessage() );
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
			
			ps.close();
			
			if (jails.get(z.name + ".Protections.EnableBlockDestroyProtection") == null) jails.set(z.name + ".Protections.EnableBlockDestroyProtection", true);
			try {
				jails.save(new File(Jail.instance.getDataFolder(),"jails.yml"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} catch (SQLException e) {
			Jail.log.log(Level.SEVERE,"[Jail] Error while creating Jail Zone! - " + e.getMessage() );
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
			
			ps.close();
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
			
			ps.close();
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
			PreparedStatement ps = conn.prepareStatement("INSERT INTO jail_prisoners  (PlayerName, RemainTime, JailName, Offline, TransferDest, reason, muted, Inventory, Jailer, Permissions, PreviousPosition) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
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
			ps.setString(10, p.getOldPermissionsString());
			if (p.getPreviousPosition() == null)
				ps.setString(11, "");
			else
				ps.setString(11, p.getPreviousPosition().getWorld().getName() + "," + String.valueOf(p.getPreviousPosition().getBlockX()) + "," + String.valueOf(p.getPreviousPosition().getBlockY()) + "," + String.valueOf(p.getPreviousPosition().getBlockZ()));
			ps.executeUpdate();
			
			conn.commit();
			
			ps.close();
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
			PreparedStatement ps = conn.prepareStatement("INSERT INTO jail_cells (JailName, Teleport, Sign, Chest, Player, Name) VALUES (?,?,?,?,?,?)");
			ps.setString(1, c.getJail().getName());
			
			ps.setString(2, String.valueOf(c.getTeleportLocation().getX()) + "," + String.valueOf(c.getTeleportLocation().getY()) + "," + String.valueOf(c.getTeleportLocation().getZ()));
			
			String signs = "";
			for (Sign s : c.getSigns())
				signs += String.valueOf(s.getBlock().getLocation().getBlockX()) + "," + String.valueOf(s.getBlock().getLocation().getBlockY()) + "," + String.valueOf(s.getBlock().getLocation().getBlockZ()) + ";";
			ps.setString(3, signs);
			
			if (c.getChest() != null)
				ps.setString(4, String.valueOf(c.getChest().getX()) + "," + String.valueOf(c.getChest().getY()) + "," + String.valueOf(c.getChest().getZ()));
			else
				ps.setString(4, "");

			if (c.getName() != null)
				ps.setString(6, c.getName());
			else
				ps.setString(6, "");

			

			ps.setString(5, c.getPlayerName());
			ps.executeUpdate();
			conn.commit();
			
			ps.close();

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
			PreparedStatement ps = conn.prepareStatement("UPDATE jail_cells SET JailName = ?, Teleport = ?, Sign = ?, Chest = ?, Player = ?, Name = ? WHERE Teleport = ?");
			ps.setString(1, c.getJail().getName());
			
			ps.setString(2, String.valueOf(c.getTeleportLocation().getX()) + "," + String.valueOf(c.getTeleportLocation().getY()) + "," + String.valueOf(c.getTeleportLocation().getZ()));

			String signs = "";
			for (Sign s : c.getSigns())
				signs += String.valueOf(s.getBlock().getLocation().getBlockX()) + "," + String.valueOf(s.getBlock().getLocation().getBlockY()) + "," + String.valueOf(s.getBlock().getLocation().getBlockZ()) + ";";
			ps.setString(3, signs);
			
			if (c.getChest() != null)
				ps.setString(4, String.valueOf(c.getChest().getX()) + "," + String.valueOf(c.getChest().getY()) + "," + String.valueOf(c.getChest().getZ()));
			else
				ps.setString(4, "");

			ps.setString(5, c.getPlayerName());
			if (c.getName() != null)
				ps.setString(6, c.getName());
			else
				ps.setString(6, "");

			ps.setString(7, String.valueOf(c.oldteleport.getX()) + "," + String.valueOf(c.oldteleport.getY()) + "," + String.valueOf(c.oldteleport.getZ()));
			ps.executeUpdate();
			conn.commit();
			
			ps.close();

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
			
			ps.close();
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
			PreparedStatement ps = conn.prepareStatement("UPDATE jail_prisoners SET RemainTime = ?, JailName = ?, Offline = ?, TransferDest = ?, muted = ?, Inventory = ?, Permissions = ?, PreviousPosition = ? WHERE PlayerName = ?");
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
			ps.setString(7, p.getOldPermissionsString());
			if (p.getPreviousPosition() == null)
				ps.setString(8, "");
			else
				ps.setString(8, p.getPreviousPosition().getWorld().getName() + "," + String.valueOf(p.getPreviousPosition().getBlockX()) + "," + String.valueOf(p.getPreviousPosition().getBlockY()) + "," + String.valueOf(p.getPreviousPosition().getBlockZ()));

			ps.setString(9, p.getName());
			ps.executeUpdate();
			conn.commit();
			
			ps.close();

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
			
			ps.close();
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
			PreparedStatement ps = conn.prepareStatement("UPDATE jail_prisoners SET RemainTime = ?, JailName = ?, Offline = ?, TransferDest = ?, muted = ?, Inventory = ?, Permissions = ?, PreviousLocation = ? WHERE PlayerName = ?");
			for (JailPrisoner p : Jail.prisoners.values())
			{
				ps.setInt(1, p.getRemainingTime());
				ps.setString(2, p.getJail().getName());
				ps.setBoolean(3, p.offlinePending());
				ps.setString(4, p.getTransferDestination());
				ps.setBoolean(5, p.isMuted());
				ps.setString(6, p.getInventory());
				ps.setString(7, p.getOldPermissionsString());
				if (p.getPreviousPosition() == null)
					ps.setString(8, "");
				else
					ps.setString(8, p.getPreviousPosition().getWorld().getName() + "," + String.valueOf(p.getPreviousPosition().getBlockX()) + "," + String.valueOf(p.getPreviousPosition().getBlockY()) + "," + String.valueOf(p.getPreviousPosition().getBlockZ()));
				ps.executeUpdate();


				
				ps.setString(9, p.getName());
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();

			ps.close();
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
            	if (Settings.getGlobalBoolean(Setting.UseMySQL))
                {
                	st.executeUpdate("CREATE TABLE IF NOT EXISTS `jail_prisoners` ( `PlayerName` varchar(250) NOT NULL, `RemainTime` int(11) DEFAULT NULL, `JailName` varchar(250) DEFAULT NULL, `Offline` varchar(250) DEFAULT NULL, `TransferDest` varchar(250) DEFAULT NULL , `reason` varchar(250) DEFAULT NULL, `muted` TINYINT DEFAULT false, Inventory TEXT DEFAULT NULL, Jailer VARCHAR(250) DEFAULT NULL, Permissions VARCHAR(250) DEFAULT NULL, PreviousPosition VARCHAR(250) DEFAULT NULL, PRIMARY KEY (`PlayerName`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
                	st.executeUpdate("CREATE TABLE IF NOT EXISTS `jail_zones` ( `name` varchar(250) NOT NULL DEFAULT '', `X1` double DEFAULT NULL, `Y1` double DEFAULT NULL, `Z1` double DEFAULT NULL, `X2` double DEFAULT NULL, `Y2` double DEFAULT NULL, `Z2` double DEFAULT NULL, `teleX` double DEFAULT NULL, `teleY` double DEFAULT NULL, `teleZ` double DEFAULT NULL, `freeX` double DEFAULT NULL, `freeY` double DEFAULT NULL, `FreeZ` double DEFAULT NULL, `teleWorld` varchar(250) DEFAULT NULL, `freeWorld` varchar(250) DEFAULT NULL , PRIMARY KEY (`name`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
                	st.executeUpdate("CREATE TABLE IF NOT EXISTS `jail_cells` ( `JailName` varchar(250) NOT NULL, `Teleport` varchar(250) NOT NULL, `Sign` TEXT DEFAULT NULL , `Chest` varchar(250) DEFAULT NULL, Player varchar(250) DEFAULT NULL, Name varchar(20) DEFAULT NULL, PRIMARY KEY (Teleport) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
                }
                else
                {
                	st.executeUpdate("CREATE TABLE IF NOT EXISTS \"jail_prisoners\" (\"PlayerName\" VARCHAR PRIMARY KEY  NOT NULL , \"RemainTime\" INTEGER, \"JailName\" VARCHAR, \"Offline\" BOOLEAN, \"TransferDest\" VARCHAR, `reason` VARCHAR, `muted` BOOLEAN, Inventory STRING, Jailer VARCHAR, Permissions VARCHAR, PreviousPosition VARCHAR)");
                    st.executeUpdate("CREATE TABLE IF NOT EXISTS \"jail_zones\" (\"name\" VARCHAR PRIMARY KEY  NOT NULL , \"X1\" DOUBLE, \"Y1\" DOUBLE, \"Z1\" DOUBLE, \"X2\" DOUBLE, \"Y2\" DOUBLE, \"Z2\" DOUBLE, \"teleX\" DOUBLE, \"teleY\" DOUBLE, \"teleZ\" DOUBLE, \"freeX\" DOUBLE, \"freeY\" DOUBLE, \"FreeZ\" DOUBLE, \"teleWorld\" VARCHAR, \"freeWorld\" STRING)");
                    st.executeUpdate("CREATE TABLE IF NOT EXISTS `jail_cells` ( `JailName` VARCHAR NOT NULL,  `Teleport` VARCHAR  PRIMARY_KEY NOT NULL, `Sign` STRING DEFAULT NULL , `Chest` VARCHAR DEFAULT NULL, Player VARCHAR DEFAULT NULL, Name VARCHAR DEFAULT NULL);");
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
    	Update("SELECT Name FROM jail_cells", "ALTER TABLE jail_cells ADD Name VARCHAR", "ALTER TABLE jail_cells ADD Name varchar(20);"); //Select specific cell when jailing - 1.2
    	UpdateType("jail_cells", "Sign", "TEXT"); // Multiple signs - 2.0
    	Update("SELECT Permissions FROM jail_prisoners", "ALTER TABLE jail_prisoners ADD Permissions VARCHAR;", "ALTER TABLE jail_prisoners ADD Permissions varchar(250);" ); //Store permissions - 3.0
    	Update("SELECT PreviousPosition FROM jail_prisoners", "ALTER TABLE jail_prisoners ADD PreviousPosition VARCHAR;", "ALTER TABLE jail_prisoners ADD PreviousPosition varchar(250);" ); //Store position - 2.0
    	DeleteField("jail_cells", "SecondChest");
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
    			if (Settings.getGlobalBoolean(Setting.UseMySQL))
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
    		if (!Settings.getGlobalBoolean(Setting.UseMySQL)) return;
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
    
    public void DeleteField(String table, String field)
    {
    	if (!Settings.getGlobalBoolean(Setting.UseMySQL)) return; //This can't be done in SQLite
    	
    	try
    	{
    		Statement statement = getConnection().createStatement();
			statement.executeQuery("SELECT " + field + " FROM " + table);
			statement.close();
    	}
    	catch(SQLException ex)
    	{
    		return;
    	}
    	
    	Jail.log.log(Level.INFO, "[Jail] Updating database");
		try {
        	Connection conn = getConnection();
			Statement st = conn.createStatement();
			st.executeUpdate("ALTER Table " + table + " DROP " + field);
			conn.commit();
			st.close();
		} catch (SQLException e) {
			Jail.log.log(Level.SEVERE, "[Jail] Error while updating tables to the new version - " + e.getMessage());
            e.printStackTrace();
		}
    }
    
    public void initMetrics()
    {
    	try {
    	    Metrics metrics = new Metrics(Jail.instance);

    	    // Add our plotters
    	    metrics.addCustomData(new Metrics.Plotter("Total people jailed") {
    	        @Override
    	        public int getValue() {
    	            return Jail.prisoners.size();
    	        }
    	    });

    	    metrics.start();
    	} catch (IOException e) {
			Jail.log.log(Level.SEVERE, "[Jail] Error while initializing Metrics - " + e.getMessage());
			e.printStackTrace();
    	}
    }

}
