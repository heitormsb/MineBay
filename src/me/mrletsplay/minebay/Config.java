package me.mrletsplay.minebay;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.md_5.bungee.api.ChatColor;

public class Config {

	private static File ConfigFile = new File("plugins/MineBay", "Config.yml");
	public static FileConfiguration Config = YamlConfiguration.loadConfiguration(ConfigFile); 
	
	public static void save(){
		try{
			Config.save(ConfigFile);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void init(){
		Config.addDefault("minebay.prefix", "&8[&6Mine&bBay&8]");
		Config.addDefault("minebay.mbstring", "&6Mine&bBay");
		Config.addDefault("minebay.info.purchase.success", "%prefix% &aYou successfully bought &6%amount% %type% &afrom &6%seller% &afor &6%price% %currency%");
		Config.addDefault("minebay.info.purchase.error", "&cError: %error%");
		Config.addDefault("minebay.info.sell.success", "%prefix% &aSuccessfully put &6%amount% %type% &afor &6%price% %currency% &afor sale on %mbstring%");
		Config.addDefault("minebay.info.sell.seller.success", "%prefix% &6%buyer% &ahas bought &6%amount% %type% &afor &6%price% %currency% &afrom you on %mbstring% &7(You get %price2% %currency%)");
		Config.addDefault("minebay.info.sell.owner.success", "%prefix% &6%buyer% &ahas bought &6%amount% %type% &afor &6%price% %currency% &ain your room on %mbstring% &7(You get %price2% %currency%)");
		Config.addDefault("minebay.info.sell.error.noitem", "%prefix% &cYou need to hold an item in your hand");
		Config.addDefault("minebay.info.sell.error.toocheap", "%prefix% &cYou need to set a price higher than 0");
		Config.addDefault("minebay.info.sell.error.no-slots", "%prefix% &cAll slots are already occupied");
		Config.addDefault("minebay.info.sell.error.too-many-sold", "%prefix% &cYou have already sold too many items in that room");
		Config.addDefault("minebay.info.newname", "%prefix% &aType in a new name (Max. %maxchars% Characters)");
		Config.addDefault("minebay.info.newname-applied", "%prefix% &aName changed to: %newname%");
		Config.addDefault("minebay.info.error.name-too-long", "%prefix% &cMaximum name length: %maxchars%");
		Config.addDefault("minebay.info.newblock-applied", "%prefix% &aBlock changed to: %type%");
		Config.addDefault("minebay.info.room-created", "%prefix% &aRoom &6\"%name%\" &acreated! &7(Properties: Tax: %taxshare%%, Slots: %slots%, Icon Material: %iconmaterial%, ID: %roomid%)");
		Config.addDefault("minebay.info.room-create.error.too-many-rooms", "%prefix% &cYou have already reached the room limit!");
		Config.addDefault("minebay.info.slot-buy.success", "%prefix% &aBought one slot for %slotprice% %currency%");
		Config.addDefault("minebay.info.slot-buy.error", "%prefix% &cError: %error%");
		Config.addDefault("minebay.info.slot-buy.toomanyslots", "%prefix% &cYou already have reached the maximum amount of slots");
		Config.addDefault("minebay.info.slot-sell.success", "%prefix% &aSold one slot for %slotprice% %currency%");
		Config.addDefault("minebay.info.slot-sell.error", "%prefix% &cError: %error%");
		Config.addDefault("minebay.info.slot-sell.notenoughslots", "%prefix% &cYou already have reached the minimum amount of slots");
		Config.addDefault("minebay.info.tax.success", "%prefix% &aChanged the tax to %newtax%%");
		Config.addDefault("minebay.info.tax.toohigh", "%prefix% &cYou already have reached the maximum tax");
		Config.addDefault("minebay.info.tax.toolow", "%prefix% &cYou can't set the tax below 0%");
		Config.addDefault("minebay.info.sell-room.success", "%prefix% &aSuccessfully sold your room for %price% %currency%");
		Config.addDefault("minebay.info.sell-room.not-empty", "%prefix% &cThere are still offers in your room");
		Config.addDefault("minebay.info.sell-room.error", "%prefix% &cError: %error%");
		Config.addDefault("minebay.info.retract-sale.success", "%prefix% &aSuccessfully retracted your sale");
		//Config.addDefault("minebay.user-rooms.enable", true);
		Config.addDefault("minebay.default-auction-room.slots", -1);
		Config.addDefault("minebay.default-auction-room.taxshare", 5);
		Config.addDefault("minebay.default-auction-room.name", "Default Auction Room");
		Config.addDefault("minebay.default-auction-room.icon-material", "GRASS");
		Config.addDefault("minebay.default-auction-room.applySettings", false);
		Config.addDefault("minebay.user-rooms.room-price", 1000);
		Config.addDefault("minebay.user-rooms.slot-price", 100);
		Config.addDefault("minebay.user-rooms.default-tax-percent", 5);
		Config.addDefault("minebay.user-rooms.max-tax-percent", 50);
		Config.addDefault("minebay.user-rooms.default-slot-number", 5);
		Config.addDefault("minebay.user-rooms.max-slots", 50);
		Config.addDefault("minebay.user-rooms.offers-per-slot", 5);
		Config.addDefault("minebay.user-rooms.max-name-length", 20);
		Config.addDefault("minebay.user-rooms.max-rooms", 3);
		Config.addDefault("minebay.user-rooms.default-icon-material", "GRASS");
		List<String> perms = new ArrayList<>();
		perms.add("user.premium");
		perms.add("user.donator");
		Config.addDefault("room-perms", perms);
		Config.addDefault("room-perm.user.premium.max-rooms", 5);
		Config.addDefault("room-perm.user.donator.max-rooms", 7);
		Config.options().copyDefaults(true);
		save();
	}
	
	public static String simpleReplace(String s){
		String currencyName = Main.econ.currencyNamePlural();
		s = ChatColor.translateAlternateColorCodes('&', s
				.replace("%prefix%", Config.getString("minebay.prefix"))
				.replace("%mbstring%", Config.getString("minebay.mbstring")))
				.replace("%maxchars%", ""+Config.getInt("minebay.user-rooms.max-name-length"))
				.replace("%slotprice%", ""+Config.getInt("minebay.user-rooms.slot-price"));
		if(currencyName!=null){
			s = s.replace("%currency%", Main.econ.currencyNamePlural());
		}else{
			s = s.replace("%currency%", "");
		}
		return s;
	}
	
	public static String replaceForSellItem(String s, SellItem it){
		s = ChatColor.translateAlternateColorCodes('&', s
				.replace("%amount%", ""+it.getItem().getAmount())
				.replace("%type%", it.getItem().getType().toString().toLowerCase().replace("_", " "))
				.replace("%seller%", it.getSeller())
				.replace("%price%", ""+it.getPrice()));
		return s;
	}
	
	public static String replaceForAuctionRoom(String s, AuctionRoom r){
		s = ChatColor.translateAlternateColorCodes('&', s
				.replace("%name%", ""+r.getName())
				.replace("%taxshare%", ""+r.getTaxshare())
				.replace("%slots%", ""+r.getSlots())
				.replace("%roomid%", ""+r.getRoomID())
				.replace("%iconmaterial%", ""+r.getIconMaterial().name().toLowerCase().replace("_", " ")));
		return s;
	}
	
	public static String onlyDigits(String s){
		StringBuilder b = new StringBuilder();
		for(char c : s.toCharArray()){
			if(Character.isDigit(c)){
				b.append(c);
			}
		}
		return b.toString();
	}
	
	public static String onlyDigitsNoColor(String s){
		s = s.replaceAll("�.", "");
		StringBuilder b = new StringBuilder();
		for(char c : s.toCharArray()){
			if(Character.isDigit(c)){
				b.append(c);
			}
		}
		return b.toString();
	}
	
}