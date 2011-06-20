package com.matejdro.bukkit.jail;

import java.util.List;

public class Settings {

	public static int SelectionTool;
	public static List<String> ExecutedCommandsOnJail;
	public static List<String> ExecutedCommandsOnRelease;
	public static Boolean DeleteInventoryOnJail;
	public static Boolean AutomaticMute;
	public static String NearestJailCode;
	public static Boolean StoreInventory;
	public static String SignText;
	public static boolean AlwaysTeleportIntoJailCenter;
	public static boolean CanPrisonerOpenHisChest;
	
	//JailStick
	public static Boolean EnableJailStick;
	public static String JailStickParameters;
	
	//Protections
	public static Boolean BlockDestroyProtection;
	public static int BlockDestroyPenalty;
	public static Boolean BlockPlaceProtection;
	public static int BlockPlacePenalty;
	public static List<String> BlockProtectionExceptions;
	public static Boolean PlayerMoveProtection;
	public static int PlayerMovePenalty;
	public static String PlayerMoveProtectionAction;
	public static Boolean FireProtection;
	public static int FirePenalty;
	public static Boolean BucketProtection;
	public static int BucketPenalty;
	public static String[] PreventCommands;
	public static int CommandPenalty;
	public static List<String> PreventInteractionBlocks;
	public static List<String> PreventInteractionItems;
	public static Boolean ExplosionProtection;
	public static int InteractionPenalty;
	public static Boolean PreventPvPInJail;

	//JailPay
	public static Boolean EnablePaying;
	public static double PricePerMinute;
	public static double PriceForInfiniteJail;
	
	//Guards
	public static int GuardHealth;
	public static int GuardDamage;
	public static int NumberOfGuards;
	public static boolean Guardinvincibility;
	public static int GuardAttackSpeedPercent;
	public static Boolean RespawnGuards;
	public static int GuardTeleportDistance;
	
	//Messages
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
	public static String MessageInteractionPenalty;
	public static String MessageInteractionNoPenalty;

	//Database
	public static Boolean UseMySql;
	public static String MySqlConn;
	public static String MySqlUsername;
	public static String MySqlPassword;
	
	
}
