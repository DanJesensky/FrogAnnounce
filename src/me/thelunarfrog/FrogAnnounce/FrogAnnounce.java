package me.thelunarfrog.FrogAnnounce;

import java.util.*;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class FrogAnnounce extends JavaPlugin
{
	private PluginDescriptionFile pdfFile;
	private static String pt = "[FrogAnnounce] ";

	protected static YamlConfiguration Settings = Config.Settings;

	private String remove = "Remove";
	private String add = "Add";
    public static Permission permission = null;
	protected static String Tag;
	protected static int Interval, taskId = -1, counter = 0;
	protected static boolean isScheduling = false, isRandom, permissionsEnabled = false, toGroups, optedOut = false;
	protected static boolean permissionConfig;
	protected static List<String> strings, Groups, ignoredPlayers;
	protected boolean usingPerms;
	
	boolean pexEnabled, bpEnabled, pEnabled;
	static String message;
	int permissionsSystem;

	public static FrogAnnounce plugin;

    @Override
	public void onEnable()
    {
    	pdfFile = this.getDescription();
		try{
			Config.loadConfig();
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
		try{
			Config.loadConfig();
		}catch(InvalidConfigurationException e){
			System.out.println(e.getMessage());
		}
		checkPermissionsVaultPlugins();
    	out("Settings loaded " + strings.size() + " announcements!");
    	isScheduling = scheduleOn(null);
    	out("Version " + pdfFile.getVersion() + " by TheLunarFrog has been enabled!");
	}
    @Override
	public void onDisable()
    {
    	scheduleOff(true, null);
    	out("Version " + pdfFile.getVersion() + " by TheLunarFrog has been disabled!");
    }
    private boolean permission(Player player, String line, Boolean op){
    	if(permissionsEnabled) {
    		return permission.has(player, line);
    	} else {
    		return op;
    	}
    }
	private void scheduleOff(boolean disabled, Player player){
    	if(isScheduling){
    		getServer().getScheduler().cancelTask(taskId);
    		if(player != null) player.sendMessage(ChatColor.RED+"Announcements disabled!");
    		out("Announcements disabled!");
	    	isScheduling = false;
    	}else{
    		if(!disabled)
    			if(player != null) player.sendMessage(ChatColor.DARK_RED+"No annuoncements running!");
    			out("No announcements running!" );
    	}
    }
    private boolean scheduleOn(Player player){
    	if(!isScheduling){
	    	if(strings.size() > 0){
	    		taskId = getServer().getScheduler().scheduleAsyncRepeatingTask(this, new printAnnounce(), Interval * 1200, Interval * 1200);
		    	if(taskId == -1){
		    		if(player != null) player.sendMessage(ChatColor.DARK_RED+"Announcing failed!");
		    		out("Announcing failed!" );
		    		return false;
		    	}else{
		    		counter = 0;
		    		if(player != null) player.sendMessage(ChatColor.GREEN+"Scheduled to announce every "+ Interval +" minute(s)!");
		    		out("Scheduled to announce every "+ Interval +" minute(s)!" );
		    		return true;
		    	}
	    	}else{
	    		if(player != null) player.sendMessage(ChatColor.DARK_RED+"Announcing failed! There are no announcements!");
	    		out("Announcing failed! There are no announcements!" );
	    		return false;
	    	}
    	}
	else{
    		if(player != null) player.sendMessage(ChatColor.DARK_RED+"Announcer is already running.");
    		out("Announcer is already running." );
    		return true;
    	}
    }
    private void scheduleRestart(Player player){
    	if(isScheduling){
    		scheduleOff(false, null);
    		try {
				Config.loadConfig();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
    		player.sendMessage(ChatColor.DARK_GREEN + "FrogAnnounce has been successfully reloaded!");
    		player.sendMessage(ChatColor.DARK_GREEN+"Settings loaded "+strings.size()+" announcements!");
    		isScheduling = scheduleOn(player);
    	}else{
    		player.sendMessage(ChatColor.DARK_RED+"No announcements running!");
    	}
    }
    private void setInterval(String[] args, Player player){
    	if(args.length == 2) {
    		try{
				int interval = Integer.parseInt(args[1], 10);
				Settings.set("Settings.Interval", interval);
				try {
					Config.saveConfig();
				} catch (InvalidConfigurationException e) {
					e.printStackTrace();
				}
				player.sendMessage(ChatColor.DARK_GREEN+"Interval changed successfully to "+args[1]+" minutes.");
				if(isScheduling) player.sendMessage(ChatColor.GOLD+"Restart the schedule to apply changes.");
			}catch(NumberFormatException err){
				player.sendMessage(ChatColor.DARK_RED+"Error! Usage: /fa interval 5");
			}
    	}else{
    		player.sendMessage(ChatColor.DARK_RED+"Error! Usage: /fa interval 5");
    	}
    }
    private void setRandom(String[] args, Player player){
    	if(args.length == 2) {
    		if(args[1].equals("on") || args[1].equals("true")){
    			Config.Settings.set("Settings.Random", true);
    			try {
					Config.saveConfig();
				} catch (InvalidConfigurationException e) {
					e.printStackTrace();
				}
    			player.sendMessage(ChatColor.DARK_GREEN+"Changed to shuffle-order mode.");
    			if(isScheduling) player.sendMessage(ChatColor.GOLD+"Restart the schedule to apply changes.");
    		}else if(args[1].equals("off") || args[1].equals(false)){
    			Settings.set("Settings.Random", false);
    			try {
					Config.saveConfig();
				} catch (InvalidConfigurationException e) {
					e.printStackTrace();
				}
    			player.sendMessage(ChatColor.DARK_GREEN+"Changed to consecutive cylcing mode.");
    			if(isScheduling) player.sendMessage(ChatColor.AQUA+"Restart the schedule to apply changes.");
    		}else{
    			player.sendMessage(ChatColor.DARK_RED+"Error! Usage: /fa random off");
    		}
    	}else{
    		player.sendMessage(ChatColor.DARK_RED+"Error! Usage: /fa random off");
    	}
    }
    @Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
		String commandName = cmd.getName();
    		Player player = (Player)sender;
    		if(commandLabel.equalsIgnoreCase("fa") || commandLabel.equalsIgnoreCase("frogannounce"))
    		{
    			if(permission(player, "frogannounce.admin", player.isOp()) || permission(player, "frogannounce.*", player.isOp())) {
	    			try {
	    				if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?") || args[0].isEmpty())
	    					auctionHelp(player);
	    				else if(args[0].equalsIgnoreCase("on"))
	    					isScheduling = scheduleOn(player);
	    				else if(args[0].equalsIgnoreCase("interval") || args[0].equalsIgnoreCase("int"))
	    					setInterval(args, player);
	    				else if(args[0].equalsIgnoreCase("random") || args[0].equalsIgnoreCase("rand"))
	    					setRandom(args, player);
	    				else if(args[0].equalsIgnoreCase("restart") || args[0].equalsIgnoreCase("reload")){
	    					scheduleRestart(player);
	    					reloadConfig();
	    				}
	    				return true;
	    			}
	    			catch(ArrayIndexOutOfBoundsException e) {
	    				return false;
	    			}
    			}
    			if(permission(player, "frogannounce", player.isOp()) || permission(player, "frogannounce.admin", player.isOp())){
    				if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))
    					auctionHelp(player);
    				}
    			if(permission(player, "frogannounce.optout", player.isOp()) || permission(player, "frogannounce.admin", player.isOp())){
    					if(args[1].equalsIgnoreCase("optout") || args[1].equalsIgnoreCase("oo")){
//    						optOut(true, player);
    					}else if(args[1].equalsIgnoreCase("optin")){
//    						optOut(false, player);
    					}
    			}
    			else {
    				player.sendMessage(ChatColor.RED + "You do not have the permission level required to use this command!");
    				return true;
    	    	}
    		}
    	return false;
    }
    public void auctionHelp(Player player) {
    	String or = ChatColor.WHITE + "|";
    	String auctionStatusColor = ChatColor.DARK_GREEN.toString();
    	String helpMainColor = ChatColor.GOLD.toString();
    	String helpCommandColor = ChatColor.AQUA.toString();
    	String helpObligatoryColor = ChatColor.DARK_RED.toString();
        player.sendMessage(helpMainColor 	+ " * " 			+ auctionStatusColor 	+ "Help for FrogAnnounce" 			+ helpMainColor	+ " * ");
        player.sendMessage(helpCommandColor + "/fa <help" 		+ or + helpCommandColor + "?>" 		+ helpMainColor 		+ " - Show this message.");
        player.sendMessage(helpCommandColor + "/fa <on" 		+ or + helpCommandColor + "off>" 	+ helpMainColor 		+ " - Start or stop FrogAnnounce.");
        player.sendMessage(helpCommandColor + "/fa <restart" 	+ or + helpCommandColor + "reload>" + helpMainColor 		+ " - Restart FrogAnnounce.");
        player.sendMessage(helpCommandColor + "/fa <interval" 	+ or + helpCommandColor + "int>" 	+ helpObligatoryColor 	+ " <minutes>" 	+ helpMainColor			   + " - Set the time between each announcement.");
        player.sendMessage(helpCommandColor + "/fa <random" 	+ or + helpCommandColor + "rand>"	+ helpObligatoryColor 	+ " <on" 		+ or + helpObligatoryColor + "off>" + helpMainColor + " - Set random or consecutive.");
    }
	protected static String colorize(String announce)
	{
		announce = announce.replaceAll("&AQUA;",		ChatColor.AQUA.toString());
		announce = announce.replaceAll("&BLACK;",		ChatColor.BLACK.toString());
		announce = announce.replaceAll("&BLUE;",		ChatColor.BLUE.toString());
		announce = announce.replaceAll("&DARK_AQUA;",	ChatColor.DARK_AQUA.toString());
		announce = announce.replaceAll("&DARK_BLUE;",	ChatColor.DARK_BLUE.toString());
		announce = announce.replaceAll("&DARK_GRAY;",	ChatColor.DARK_GRAY.toString());
		announce = announce.replaceAll("&DARK_GREEN;", 	ChatColor.DARK_GREEN.toString());
		announce = announce.replaceAll("&DARK_PURPLE;",	ChatColor.DARK_PURPLE.toString());
		announce = announce.replaceAll("&RED;", 		ChatColor.RED.toString());
		announce = announce.replaceAll("&DARK_RED;",	ChatColor.DARK_RED.toString());
		announce = announce.replaceAll("&GOLD;",		ChatColor.GOLD.toString());
		announce = announce.replaceAll("&GRAY;",		ChatColor.GRAY.toString());
		announce = announce.replaceAll("&GREEN;",		ChatColor.GREEN.toString());
		announce = announce.replaceAll("&LIGHT_PURPLE;",ChatColor.LIGHT_PURPLE.toString());		
		announce = announce.replaceAll("&b;",			ChatColor.AQUA.toString());
		announce = announce.replaceAll("&0;",			ChatColor.BLACK.toString());
		announce = announce.replaceAll("&9;",			ChatColor.BLUE.toString());
		announce = announce.replaceAll("&3;",			ChatColor.DARK_AQUA.toString());
		announce = announce.replaceAll("&1;",			ChatColor.DARK_BLUE.toString());
		announce = announce.replaceAll("&8;",			ChatColor.DARK_GRAY.toString());
		announce = announce.replaceAll("&2;", 			ChatColor.DARK_GREEN.toString());
		announce = announce.replaceAll("&5;",			ChatColor.DARK_PURPLE.toString());
		announce = announce.replaceAll("&4;",			ChatColor.DARK_RED.toString());
		announce = announce.replaceAll("&6;",			ChatColor.GOLD.toString());
		announce = announce.replaceAll("&7;",			ChatColor.GRAY.toString());
		announce = announce.replaceAll("&a;",			ChatColor.GREEN.toString());
		announce = announce.replaceAll("&d;",			ChatColor.LIGHT_PURPLE.toString());
		announce = announce.replaceAll("&c;",			ChatColor.RED.toString());
		announce = announce.replaceAll("&f;",			ChatColor.WHITE.toString());
		announce = announce.replaceAll("&e;",			ChatColor.YELLOW.toString());
		return announce;

	}
    class printAnnounce implements Runnable
    {
        @Override
		public void run()
        {
        	String announce = "";
        	
        	if(isRandom)
        	{
	            Random randomise = new Random();
	            int selection = randomise.nextInt(strings.size());
	            announce = strings.get(selection);
        	}else{
        		announce = strings.get(counter);
        		counter++;
        		if(counter >= strings.size())
        			counter = 0;
        	}
        	if(permissionConfig && toGroups){
        		Player[] players = getServer().getOnlinePlayers();
       			for(Player p: players){
       				for(String group: Groups){
       					if(permission.playerInGroup(p.getWorld().getName(), p.getName(), group)){
       						for (String line : announce.split("&NEW_LINE;"))
       							p.sendMessage(Tag+" "+colorize(line));
       						break;
       					}
        			}
        		}
        	}
        	else{
					for (String line : announce.split("&NEW_LINE;"))
        			getServer().broadcastMessage(Tag+" "+colorize(line));
        	}
        }
    }
    public boolean isDebugging(Player player)
    {
        if(debugees.containsKey(player)){
            return ((Boolean)debugees.get(player)).booleanValue();
        }
        else{
            return false;
        }
    }
    public void setDebugging(Player player, boolean value)
    {
        debugees.put(player, Boolean.valueOf(value));
    }

    //1.6
    protected void broadcastAnnouncement(int index){
    	String announce = " ";
    		announce = strings.get(index);
    		
    		if(counter > strings.size())
    			out("Attempted to broadcast a message, but the message doesn't exist.");
    			counter = 0;
    	if(permissionConfig && toGroups){
    		Player[] players = getServer().getOnlinePlayers();
   			for(Player p: players){
   				for(String group: Groups){
   					if(permission.playerInGroup(p.getWorld().getName(), p.getName(), group)){
   						for (String line : announce.split("&NEW_LINE;"))
   							p.sendMessage(Tag+" "+colorize(line));
   						break;
   					}
    			}
    		}
    	}
    	else{
				for (String line : announce.split("&NEW_LINE;"))
    			getServer().broadcastMessage(Tag+" "+colorize(line));
    	}
    }
    protected String getTag(){
    	if(Tag.isEmpty()){
    		return null;
    	}else{
    		return Tag;
    	}
    }
    protected void changeAnnouncements(String ar, String[] args, int stringIndex) throws InvalidConfigurationException{
    	CommandSender sender = null;
		int m = strings.size();
		if(stringIndex > m){
			sender.sendMessage("[FrogAnnounce] The specified announcement index does not exist.");
			out(sender.getName()+" attempted to add or remove an announcement from file, but the announcement index doesn't exist.");
		}else{    	
			if(ar != remove){
//				plugin.getConfig().getList("Announcer.Strings").add("- '"+args+"'");
    			Config.saveConfig();
			}else if(ar != add){
				plugin.getConfig().getList("Announcer.Strings").remove(stringIndex);
				Config.saveConfig();
    		}
		}
    }
    protected void removeAnnouncementFromFile(int index) throws InvalidConfigurationException{
    	Config.saveConfig();
    }
    protected void listAnnouncements(CommandSender sender) throws InvalidConfigurationException{
// 
    	Config.loadConfig();
    	this.out("Announcements loaded:"+strings.size());
    	sender.sendMessage("Announcements:"+strings.toString());
    }
    //1.6: new string-out methods
    protected void out(String message){
    	System.out.println(pt + message);
    }
    private boolean checkPEX() {
		boolean PEX = false;
		Plugin test = this.getServer().getPluginManager().getPlugin("PermissionsEX");
		if (test != null){
			PEX = true;
		}
		
		return PEX;
	}
    private boolean checkPerms(){
		boolean NP = false;
		Plugin nijikoPermissions = this.getServer().getPluginManager().getPlugin("Permissions");
		if (nijikoPermissions != null){
			NP = true;
		}
		return NP;
	}
	private boolean checkbPerms(){
		boolean bP = false;
		Plugin bPermissions = this.getServer().getPluginManager().getPlugin("bPermissions");
		if(bPermissions != null){
			bP = true;
		}
		return bP;
	}
	private int getPermissionsSystem(){
		int permissionsSystem;
		boolean usingPEX = checkPEX();
		boolean usingbPerms = checkbPerms();
		boolean usingPerms = checkPerms();
		if(usingPEX = true){
			pexEnabled = true;
			permissionsSystem = 1;
		}else if(usingbPerms = true){
			bpEnabled = true;
			permissionsSystem = 2;
		}else if(usingPerms = true){
			pEnabled = true;
			permissionsSystem = 3;
		}else{
			permissionsSystem = 0;
		}
		return permissionsSystem;
	}
	public void checkPermissionsVaultPlugins(){
		int m = getPermissionsSystem();
		Plugin vault = this.getServer().getPluginManager().getPlugin("Vault");
		if(vault != null){
			if(m!=0){
				setupPermissions();
				Boolean q = setupPermissions();
				if(setupPermissions()!=null){
					out("Permissions plugin hooked.");
					usingPerms = true;
				}else if(q = null){
					out("Permissions plugin wasn't found. Defaulting to OP/Non-OP system.");
					usingPerms = false;
				}
			}
		}else{
			out("Vault is not in your plugins directory! This plugin has a soft dependency of Vault, but if you don't have it, this will still work (you just can't use permission-based stuff).");
		}
	}
    private Boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
    protected void out(Permission plugin){
    	System.out.println(pt + permission);
    }
    private final HashMap<Object, Object> debugees = new HashMap<Object, Object>();
}