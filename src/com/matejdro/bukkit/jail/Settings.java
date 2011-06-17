package com.matejdro.bukkit.jail;

import java.util.Arrays;
import java.util.List;

public class Settings {

	public static int SelectionTool;
	public static Boolean BlockDestroyProtection;
	public static int BlockDestroyPenalty;
	public static Boolean BlockPlaceProtection;
	public static int BlockPlacePenalty;
	public static List<String> BlockProtectionExceptions = Arrays.asList(new String[]{"59"});
	public static Boolean PlayerMoveProtection;
	public static int PlayerMovePenalty;
	public static String PlayerMoveProtectionAction;
	public static Boolean FireProtection;
	public static int FirePenalty;
	public static Boolean BucketProtection;
	public static int BucketPenalty;
	public static String[] PreventCommands;
	public static int CommandPenalty;
	public static List<String> PreventInteractionBlocks = Arrays.asList(new String[]{"69", "72", "70", "77", "46", "64", "96"});
	public static List<String> PreventInteractionItems = Arrays.asList(new String[]{"357"});
	public static Boolean ExplosionProtection = true;
	public static int InteractionPenalty = 10;
	public static Boolean PreventPvPInJail = true;
	public static List<String> ExecutedCommandsOnJail = Arrays.asList(new String[]{"deop <Player>"});
	public static List<String> ExecutedCommandsOnRelease = Arrays.asList(new String[]{""});
	public static Boolean DeleteInventoryOnJail;
	public static Boolean AutomaticMute;
	public static String NearestJailCode;
	public static Boolean StoreInventory;
	public static String SignText;
	public static Boolean EnableJailStick;
	public static String JailStickParameters;
	public static Boolean EnableEscape;
	public static boolean AlwaysTeleportIntoJailCenter;
	public static boolean CanPrisonerOpenHisChest;

	public static Boolean EnablePaying = true;
	public static double PricePerMinute = 10;
	public static double PriceForInfiniteJail = 9999;
	
	public static int GuardHealth;
	public static int GuardDamage;
	public static int NumberOfGuards;
	public static boolean Guardinvincibility;
	public static int GuardAttackSpeedPercent;
	public static Boolean RespawnGuards;
	public static int GuardTeleportDistance;
	
	public static String MessageJail;
	public static String MessageJailReason;
	public static String MessageUnjail;
	public static String MessageDestroyPenalty;
	public static String MessageDestroyNoPenalty;
	public static String MessageMovePenalty;
	public static String MessageMoveNoPenalty;
	public static String MessagePlacePenalty;
	public static String MessagePlaceNoPenalty;
	public static String MessageCommandPenalty;
	public static String MessageCommandNoPenalty;
	public static String MessageFirePenalty;
	public static String MessageFireNoPenalty;
	public static String MessageBucketPenalty;
	public static String MessageBucketNoPenalty;
	public static String MessageTransfer;
	public static String MessageMute;
	public static String MessageInteractionPenalty = "Don't do that in Jail!  You have just earned additional 10 minutes in jail!";
	public static String MessageInteractionNoPenalty = "Don't do that in Jail!";

	
	public static Boolean UseMySql;
	public static String MySqlConn;
	public static String MySqlUsername;
	public static String MySqlPassword;
	
	
}
