package com.matejdro.bukkit.jail;

import java.util.ArrayList;
import java.util.Arrays;

public enum Setting {
	
	SelectionTool("SelectionTool", 268),
	ExecutedCommandsOnJail("ExecutedCommandsOnJail", new ArrayList<String>()),
	ExecutedCommandsOnRelease("ExecutedCommandsOnRelease", new ArrayList<String>()),
	DeleteInventoryOnJail("DeleteInventoryOnJail", false),
	AutomaticMute("AutomaticMute", false),
	NearestJailCode("NearestJailCode", "nearest"),
	StoreInventory("StoreInventory", true),
	SignText("SignText", "<Player>[NEWLINE]<TimeS>[NEWLINE]for[NEWLINE]<Reason>"),
	CanPrisonerOpenHisChest("CanPrisonerOpenHisChest", true),
	LogJailingIntoConsole("LogJailingIntoConsole", false),
	CountdownTimeWhenOffline("CountdownTimeWhenOffline", false),
	SpoutChangeSkin("SpoutChangeSkin", false),
	SpoutSkinChangeURL("SpoutSkinChangeURL", "http://www.minecraftskins.com/view/168701.png"),
	ManualJails("ManualJails", new ArrayList<String>()),
	ManualCells("ManualCells", new ArrayList<String>()),
	ReleaseBackToPreviousPosition("ReleaseBackToPreviousPosition", false),
	IgnorePrisonersSleepingState("IgnorePrisonersSleepingState", true),
	TeleportPrisonerOnRelease("TeleportPrisonerOnRelease", true),
	DefaultJailTime("DefaultJailTime", -1),

	//JailStick
	EnableJailStick("EnableJailStick", false),
	JailStickParameters("JailStickParameters", "280,5,10,,police;50,5,20,,admin"),
	
	//Protections
	EnableBlockDestroyProtection("Protections.EnableBlockDestroyProtection", true),
	BlockDestroyPenalty("Protections.BlockDestroyPenalty", 15),
	EnableBlockPlaceProtection("Protections.EnableBlockPlaceProtection", true),
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
	EnableExplosionProtection("Protections.EnableExplosionProtection", true),
	EnablePVPProtection("Protections.EnablePVPProtection", true),
	EnableChangingPermissions("Protections.EnableChangingPermissions", false),
	PrisonersPermissionsGroups("Protections.PrisonersPermissionsGroups", Arrays.asList("prisoners")),
	RestorePermissionsToEscapedPrisoners("Protections.RestorePermissionsToEscapedPrisoners", true),
	MaximumAFKTime("Protections.MaximumAFKTime", 5.0),
	EnableFoodControl("Protections.EnableFoodControl", true),
	FoodControlMinimumFood("Protections.FoodControlMinimumFood", 2),
	FoodControlMaximumFood("Protections.FoodControlMaximumFood", 5),

	
	//JailPay
	PricePerMinute("JailPay.PricePerMinute", 10),
	PriceForInfiniteJail("JailPay.PriceForInfiniteJail", 9999),
	JailPayCurrency("JailPay.Currency", 0),
	
	//Guards
	GuardHealth("Guards.GuardHealth", 8),
	GuardArmor("Guards.GuardArmor", 0),
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
	MessageJail("Messages.MessageJail", "&cYou have been jailed!"),
	MessageJailReason("Messages.MessageJailReason", "&cYou have been jailed! Reason: <Reason>"),
	MessageUnJail("Messages.MessageUnJail", "&2You have been released! Please respect server rules."),
	MessagePlayerNotJailed("Messages.PlayerNotJailed", "That player is not jailed!"),
	MessageYouNotJailed("Messages.MessageYouNotJailed", "You are not jailed!"),
	MessageBlockDestroyedNoPenalty("Messages.BlockDestroyedNoPenalty", "&cDo not destroy The Jail!"),
	MessageBlockDestroyedPenalty("Messages.BlockDestroyedPenalty", "&cDo not destroy The Jail! You have just earned additional 15 minutes in jail!"),
	MessageEscapeNoPenalty("Messages.MessageEscapeNoPenalty", "&cDo not try to escape out of Jail!"),
	MessageEscapePenalty("Messages.MessageEscapePenalty", "&cDo not try to escape out of Jail! You have just earned additional 30 minutes in jail!!"),
	MessageBlockPlacedNoPenalty("Messages.MessageBlockPlacedNoPenalty", "&cDo not place blocks inside Jail!"),
	MessageBlockPlacedPenalty("Messages.MessageBlockPlacedPenalty", "&cDo not place blocks inside Jail! You have just earned additional 10 minutes in jail!"),
	MessageForbiddenCommandNoPenalty("Messages.ForbiddenCommandNoPenalty", "&cDo not try to escape with commands!"),
	MessageForbiddenCommandPenalty("Messages.ForbiddenCommandPenalty", "&cDo not try to escape with commands! You have just earned additional 10 minutes in jail!"),
	MessageTransfer("Messages.ForbiddenTransfer", "&9You have been transferred to another jail!"),
	MessageMute("Messages.MessageMuted", "Stop chatting and quietly wait for the end of your sentence!"),
	MessagePreventedInteractionNoPenalty("Messages.PreventedInteractionNoPenalty", "Don't do that in Jail!"),
	MessagePreventedInteractionPenalty("Messages.PreventedInteractionPenalty", "Don't do that in Jail!  You have just earned additional 10 minutes in jail!"),
	MessageAFKKick("Messages.MessageAFKKick", "Do not be AFK in jail!"),
	MessageJailPayAmountForever("Messages.MessageJailPayAmountForever", "To get out of this mess, you will have to pay <Amount>."),
	MessageJailPayCannotPay("Mesasges.JailPayCannotPay", "Sorry, money won't help you this time."),
	MessageJailPayCannotPayHim("Mesasges.JailPayCannotPayHim", "Sorry, money won't help him this time."),
	MessageJailPayNotEnoughMoney("Messages.JailPayNotEnoughMoney", "You don't have that much money!"),
	MessageJailPayNotEnoughMoneySelected("Messages.JailPayNotEnoughMoneySelected", "That won't be enough for your sentence!"),
	MessageJailPayCost("Messages.JailPayCost", "1 minute of your sentence will cost you <MinutePrice>. That means that cost for releasing you out of the jail is <WholePrice>."),
	MessageJailPayPaidReleased("Messages.JailPayPaidReleased", "You have just payed <Amount> and saved yourself from the jail!"),
	MessageJailPayPaidReleasedHim("Messages.JailPayPaidReleasedHim", "You have just payed <Amount> and saved <Prisoner> from the jail!"),
	MessageJailPayPaidLoweredTime("Messages.JailPayLoweredTime", "You have just payed <Amount> and lowered your sentence to <NewTime> minutes!"),
	MessageJailPayPaidLoweredTimeHim("Messages.JailPayLoweredTimeHim", "You have just payed <Amount> and lowered <Prisoner>'s sentence to <NewTime> minutes!"),
	MessageMinutes("Messages.TimeMinutes", "<Time> minutes"),
	MessageForever("Messages.Forever", "ever"),
	MessageJailStatus("Messages.JailStatus", "You are jailed for <TimeS> by <Jailer>"),
	MessageJailStatusReason("Messages.JailStatusReason", "You are jailed for <TimeS> by <Jailer> because of <Reason>"),
	MessageJailStickEnabled("Messages.JailStickEnabled", "JailStick enabled!"),
	MessageJailStickDisabled("Messages.JailStickDisabled", "JailStick disabled!"),
	
	Debug("Debug", false);

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
