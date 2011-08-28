package com.matejdro.bukkit.jail;

import java.util.Arrays;

public enum Setting {
	
	SelectionTool("SelectionTool", 268),
	ExecutedCommandsOnJail("ExecutedCommandsOnJail", Arrays.asList(new String[0])),
	ExecutedCommandsOnRelease("ExecutedCommandsOnRelease", Arrays.asList(new String[0])),
	DeleteInventoryOnJail("DeleteInventoryOnJail", true),
	AutomaticMute("AutomaticMute", false),
	NearestJailCode("NearestJailCode", "nearest"),
	StoreInventory("StoreInventory", true),
	SignText("SignText", "<Player>[NEWLINE]<Time> minutes[NEWLINE]for[NEWLINE]<Reason>"),
	CanPrisonerOpenHisChest("CanPrisonerOpenHisChest", true),
	LogJailingIntoConsole("LogJailingIntoConsole", false),
	CountdownTimeWhenOffline("CountdownTimeWhenOffline", false),
	
	//JailStick
	EnableJailStick("EnableJailStick", false),
	JailStickParameters("JailStickParameters", "280,5,10,,police;50,5,20,,admin"),
	
	//Protections
	EnableBlockDestroyProtection("Protections.EnableBlockDestroyProtection", true),
	BlockDestroyPenalty("Protections.BlockDestroyPenalty", 15),
	EnableBlockPlaceProtection("Protectionss.EnableBlockPlaceProtection", true),
	BlockPlacePenalty("Protections.BlockPlacePenalty", 10),
	BlockProtectionExceptions("Protections.BlockProtectionExceptions", Arrays.asList(new String[]{"59"})),
	EnablePlayerMoveProtection("Protections.EnablePlayerMoveProtection", true),
	PlayerMoveProtectionPenalty("Protections.PlayerMoveProtectionPenalty", 30),
	PlayerMoveProtectionAction("Protections.PlayerMoveProtectionAction", "guards"),
	PreventCommands("Protections.PreventCommands", Arrays.asList(new String[] {"/spawn", "/kill", "/warp"})),
	CommandProtectionPenalty("Protections.CommandProtectionPenalty", 10),
	PreventInteractionBlocks("Protections.PreventInteractionBlocks", Arrays.asList(new String[] {"69", "72", "70", "46", "64", "96", "77"})),
	PreventInteractionItems("Protections.PreventInteractionItems", Arrays.asList(new String[] {"326", "327", "259"})),
	InteractionPenalty("Protections.InteractionPenalty", 10),
	EnableExplosionProtection("Protections.EnableExplosivesProection", true),
	EnablePVPProtection("Protections.EnablePVPProtection", true),
	
	//JailPay
	EnablePaying("JailPay.EnableJailPay", false),
	PricePerMinute("JailPay.PricePerMinute", 10),
	PriceForInfiniteJail("JailPay.PriceForInfiniteJail", 9999),
	
	//Guards
	GuardHealth("Guards.GuardHealth", 20),
	GuardDamage("Guards.GuardDamage", 2),
	NumbefOfGuards("Guards.NumberOfGuards", 3),
	GuardInvincibility("Guards.GuardInvincibility", false),
	GuardAttackSpeedPercent("Guards.GuardAttackSpeedPercent", 100),
	RespawnGuards("Guards.RespawnGuards", true),
	GuardTeleportDistance("Guards.GuardTeleportDistance", 10),
	
	//Database
	UseMySQL("Database.UseMySQL", false),
	MySQLConn("Database.MySQLConn", "jdbc:mysql://localhost:3306/minecraft"),
	MySQLUsername("Database.MySQLUSername", "root"),
	MySQLPassword("Database.MySQLPassword", "password"),
	
	//Messages
	MessageJail("Messages.MessageJail", "§cYou have been jailed!"),
	MessageJailReason("Messages.MessageJailReason", "§cYou have been jailed! Reason: <Reason>"),
	MessageUnJail("Messages.MessageUnJail", "§2You have been released! Please respect server rules."),
	MessageBlockDestroyedNoPenalty("Messages.BlockDestroyedNoPenalty", "§cDo not destroy The Jail!"),
	MessageBlockDestroyedPenalty("Messages.BlockDestroyedPenalty", "§cDo not destroy The Jail! You have just earned additional 15 minutes in jail!"),
	MessageEscapeNoPenalty("Messages.MessageEscapeNoPenalty", "§cDo not try to escape out of Jail!"),
	MessageEscapePenalty("Messages.MessageEscapePenalty", "§cDo not try to escape out of Jail! You have just earned additional 30 minutes in jail!!"),
	MessageBlockPlacedNoPenalty("Messages.MessageBlockPlacedNoPenalty", "§cDo not place blocks inside Jail!"),
	MessageBlockPlacedPenalty("Messages.MessageBlockPlacedPenalty", "§cDo not place blocks inside Jail! You have just earned additional 10 minutes in jail!"),
	MessageForbiddenCommandNoPenalty("Messages.ForbiddenCommandNoPenalty", "§cDo not try to escape with commands!"),
	MessageForbiddenCommandPenalty("Messages.ForbiddenCommandPenalty", "§cDo not try to escape with commands! You have just earned additional 10 minutes in jail!"),
	MessageTransfer("Messages.ForbiddenTransfer", "§9You have been transferred to another jail!"),
	MessageMute("Messages.MessageMuted", "Stop chatting and quietly wait for the end of your sentence!"),
	MessagePreventedInteractionNoPenalty("Messages.PreventedInteractionNoPenalty", "Don't do that in Jail!"),
	MessagePreventedInteractionPenalty("Messages.PreventedInteractionPenalty", "Don't do that in Jail!  You have just earned additional 10 minutes in jail!");
	
	
	private String name;
	private Object def;
	
	private Setting(String Name, Object Def)
	{
		name = Name;
		def = Def;
	}
	
	public String getString()
	{
		return name;
	}
	
	public Object getDefault()
	{
		return def;
	}
}
