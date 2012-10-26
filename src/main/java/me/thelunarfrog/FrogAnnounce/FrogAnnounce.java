package main.java.me.thelunarfrog.FrogAnnounce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The FrogAnnounce core. Handles loops, grabbing configuration values from ConfigurationManager, commands, and all announcements.
 * API will be found here, too.
 * @author Dan | TheLunarFrog
 * @version 2.0.10.13
 * @category main
 * 
 */
@SuppressWarnings("unused")
public class FrogAnnounce extends JavaPlugin implements ChatColourManager {
	private PluginDescriptionFile pdfFile;
	private static String pt =		 "[FrogAnnounce] ",
			igt=darkgreen+"[FrogAnnounce] "+green;

	private Logger logger = Logger.getLogger("Minecraft");
	public static Permission permission = null;
	protected static String tag;
	protected static int interval, taskId = -1, counter = 0;
	protected static boolean running = false, random, permissionsEnabled = false, toGroups;
	protected static boolean permissionConfig;
	protected static List<String> strings, Groups;
	protected boolean usingPerms;
	protected static String ChatSuiteChannel = "FrogAnnounce";
	protected static boolean useChatSuite = false;
	protected static ArrayList<String> ignoredPlayers = null;
	protected int playersIgnoredCounter;
	private String build = "20120922_1200";
	protected int permissionsSystem;
	private boolean debugging = false;
	public static FrogAnnounce plugin;

	@Override
	public void onEnable()
	{
		pdfFile = this.getDescription();
		try{
			ConfigurationHandler.loadConfig();
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
		try{
			ConfigurationHandler.loadConfig();
		}catch(InvalidConfigurationException e){
			System.out.println(e.getMessage());
		}
		checkPermissionsVaultPlugins();
		info("Settings loaded "+strings.size()+" announcements!");
		running = turnOn(null, true);
		info("Version "+pdfFile.getVersion()+" by TheLunarFrog has been enabled!");
	}
	@Override
	public void onDisable()
	{
		turnOff(true, null);
		info("Version "+pdfFile.getVersion()+" by TheLunarFrog has been disabled!");
	}
	private boolean permission(Player player, String line, Boolean op){
		if(permissionsEnabled) {
			return permission.has(player, line);
		} else {
			return op;
		}
	}
	private void turnOff(boolean disabled, Player player){
		if(running){
			getServer().getScheduler().cancelTask(taskId);
			if(player != null) player.sendMessage(red+"Announcer disabled!");
			info("The announcer has been disabled!");
			running = false;
		}else{
			if(!disabled)
				if(player != null) player.sendMessage(darkred+"The announcer is not running!");
				else warning("No announcer isn't running!");
		}
	}
	private boolean turnOn(Player player, boolean startup){
		if(!running){
			if(strings.size() > 0){
				taskId = getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Announcer(), interval * 1200, interval * 1200);
				if(taskId == -1){
					if(player != null) player.sendMessage(darkred+"Announcer failed to start!");
					severe("The announcer module has failed to start! Please check your configuration. If this does not fix it, then submit a support ticket on the BukkitDev page for FrogAnnounce.");
					return false;
				}else{
					counter = 0;
					if(player != null){
						player.sendMessage(igt+"Success! Now announcing every "+ interval +" minute(s)!");
						info(player.getName()+" reloaded FrogAnnounce's settings! Now announcing every "+ interval +" minute(s).");
					}
					return true;
				}
			}else{
				if(player != null){
					player.sendMessage(darkred+"[FrogAnnounce] Announcer failed to start! There are no announcements!");
					severe("("+player.getName()+"): Announcer failed to start! There are no announcements!");
				}else severe("Announcer has failed to start: there are no announcements!");
				return false;
			}
		}else{
			if(player != null) player.sendMessage(darkred+"Announcer is already running.");
			info("Announcer is already running." );
			return true;
		}
	}
	private void reloadPlugin(Player player){
		if(running){
			turnOff(false, null);
			try {
				ConfigurationHandler.loadConfig();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			if(player != null){
				player.sendMessage(igt+"FrogAnnounce has been successfully reloaded!");
				player.sendMessage(igt+"Settings loaded "+strings.size()+" announcements!");
				running = turnOn(player, true);
			}else{
				info("Settings loaded "+strings.size()+" announcements!");
				info("FrogAnnounce has been successfully reloaded!");
				running = turnOn(null, true);
			}
		}else{
			player.sendMessage(darkred+"No announcements running!");
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		String commandName = cmd.getName();
		if(sender instanceof Player){
			Player player = (Player)sender;
			if(commandLabel.equalsIgnoreCase("fa") || commandLabel.equalsIgnoreCase("frogannounce"))
			{
				if(permission(player, "frogannounce.admin", player.isOp()) || permission(player, "frogannounce.*", player.isOp()) || permission(player, "frogannounce.command."+commandName.toLowerCase(), player.isOp())) {
					try {
						if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?") || args[0].isEmpty())
							returnHelp(player);
						else if(args[0].equalsIgnoreCase("on"))
							running = turnOn(player, false);
						else if(args[0].equalsIgnoreCase("off"))
							turnOff(false, player);
						else if(args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("v"))
							sender.sendMessage(igt+green+"Current version: "+pdfFile.getVersion()+"\n"+igt+green+"Build string: "+build);
						else if(args[0].equalsIgnoreCase("ignore") || args[0].equalsIgnoreCase("optout") || args[0].equalsIgnoreCase("opt-out"))
							ignorePlayer(player, args[1]);
						else if(args[0].equalsIgnoreCase("unignore") || args[0].equalsIgnoreCase("optin") || args[0].equalsIgnoreCase("opt-in"))
							unignorePlayer(player, args[1]);
						else if(args[0].equalsIgnoreCase("interval") || args[0].equalsIgnoreCase("int"))
							setInterval(args, player);
						else if(args[0].equalsIgnoreCase("random") || args[0].equalsIgnoreCase("rand"))
							setRandom(args, player);
						else if(args[0].equalsIgnoreCase("broadcast") || args[0].equalsIgnoreCase("bc")){
							broadcastMessage(args[1], player);
						}else if(args[0].equalsIgnoreCase("restart") || args[0].equalsIgnoreCase("reload")){
							reloadPlugin(player);
							reloadConfig();
						}else if(args[0].equalsIgnoreCase("list")){
							for(String s: strings)
								player.sendMessage(colourizeText(""));
						}
						return true;
					}
					catch(ArrayIndexOutOfBoundsException e) {
						return false;
					}
				}
				if(permission(player, "frogannounce", player.isOp()) || permission(player, "frogannounce.admin", player.isOp())){
					if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))
						returnHelp(player);
				}
				else {
					player.sendMessage(red+"You do not have the permission level required to use this command!");
					return true;
				}
			}
		}else if(!(sender instanceof Player)){
			sender = null;
			Player console = null;
			if(commandLabel.equalsIgnoreCase("fa") || commandLabel.equalsIgnoreCase("frogannounce")){
				try {
					if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?") || args[0].isEmpty())
						returnHelp(console);
					else if(args[0].equalsIgnoreCase("on"))
						running = turnOn(console, false);
					else if(args[0].equalsIgnoreCase("off"))
						turnOff(true, console);
					else if(args[0].equalsIgnoreCase("interval") || args[0].equalsIgnoreCase("int"))
						setInterval(args, console);
					else if(args[0].equalsIgnoreCase("random") || args[0].equalsIgnoreCase("rand"))
						setRandom(args, console);
					//					else if(args[0].equalsIgnoreCase("manualbroadcast") || args[0].equalsIgnoreCase("mbc"))
					//						manualBroadcast(args, console);
					else if(args[0].equalsIgnoreCase("broadcast") || args[0].equalsIgnoreCase("bc")){
						broadcastMessage(args[1], console);
					}else if(args[0].equalsIgnoreCase("restart") || args[0].equalsIgnoreCase("reload")){
						reloadPlugin(console);
						reloadConfig();
					}
					return true;
				}
				catch(ArrayIndexOutOfBoundsException e) {
					return false;
				}
			}
		}
		return false;
	}
	public void returnHelp(Player player) {
		String or = white+"|";
		String auctionStatusColor = darkgreen;
		String helpMainColor = gold;
		String helpCommandColor = aqua;
		String helpObligatoryColor = darkred;
		if(player != null){
			player.sendMessage(helpMainColor 	+ " * " 			+ auctionStatusColor 	+ "Help for FrogAnnounce 2.1" 			+ helpMainColor	+ " * ");
			player.sendMessage(helpCommandColor+"/fa <help" 		+ or+helpCommandColor+"?>" 		+ helpMainColor 		+ " - Show this message.");
			player.sendMessage(helpCommandColor+"/fa <on" 		+ or+helpCommandColor+"off>" 	+ helpMainColor 		+ " - Start or stop FrogAnnounce.");
			player.sendMessage(helpCommandColor+"/fa <restart" 	+ or+helpCommandColor+"reload>"+helpMainColor 		+ " - Restart FrogAnnounce.");
			player.sendMessage(helpCommandColor+"/fa <interval" 	+ or+helpCommandColor+"int>" 	+ helpObligatoryColor 	+ " <minutes>" 	+ helpMainColor			  +" - Set the time between each announcement.");
			player.sendMessage(helpCommandColor+"/fa <random" 	+ or+helpCommandColor+"rand>"	+ helpObligatoryColor 	+ " <on" 		+ or+helpObligatoryColor+"off>"+helpMainColor+" - Set random or consecutive.");
			player.sendMessage(helpCommandColor+"/fa <broadcast"	+ or+helpCommandColor+"bc>"		+ helpObligatoryColor	+"<AnnouncementIndex>"+helpMainColor+" - Announces the announcement specified by the index immediately. Will not interrupt the normal order/time. Please note that this starts at 0.");
			player.sendMessage(helpCommandColor+"/fa-add "+or+"/faadd"+helpObligatoryColor+"<announcement message>"+helpMainColor+" - Adds an announcement to the list. (Command /faadd or /fa-add is not a typo; technical restrictions forced this.)");
			player.sendMessage(helpCommandColor+"/fa <remove "+or+"delete"+or+"rem"+or+"del>"+helpObligatoryColor+"<announcementIndex>"+helpMainColor+" - Removes the specified announcement (announcementIndex = announcement number from top to bottom in the file; starts at 0).");
			//			player.sendMessage(helpCommandColor+"/fa <manualbroadcast"+or+helpCommandColor+ "mbc"		+ helpObligatoryColor	+"<Message>"+helpMainColor+" - Announces a message to the entire server. Ignores groups in the config.");
		}else{
			info("***Help for FrogAnnounce 2.0 (Console Help Version)***");
			info("/fa <help|?> - You are here. Shows this message.");
			info("/fa <on|off> - Turns the announcements on or off.");
			info("/fa <reload|restart> - Reloads the configuration and grabs new announcements that were added/forgets old ones that were removed. Warning: Using this will remove any settings from the following 2 commands.");
			info("/fa <interval|int> <newAnnouncementInterval> - Sets the delay at which the plugin announces. Minutes.");
			info("/fa <random|rand> <true|false> - Sets the plugin to announce randomly or not.");
			info("/fa <broadcast|bc> <AnnouncementIndex> - Announces the announcement specified by the index immediately. Will not interrupt the normal order/time. Please note that this starts at 0.");
			//			info("/fa <manualbroadcast|mbc> <Message> - Announce text to the entire server. Ignores groups in the config.");
		}
	}
	protected static String colourizeText(String announce){
		announce = announce.replaceAll("&AQUA;",		aqua);
		announce = announce.replaceAll("&BLACK;",		black);
		announce = announce.replaceAll("&BLUE;",		blue);
		announce = announce.replaceAll("&DARK_AQUA;",	darkaqua);
		announce = announce.replaceAll("&DARK_BLUE;",	darkblue);
		announce = announce.replaceAll("&DARK_GRAY;",	darkgray);
		announce = announce.replaceAll("&DARK_GREEN;", 	darkgreen);
		announce = announce.replaceAll("&DARK_PURPLE;",	darkpurple);
		announce = announce.replaceAll("&RED;", 		red);
		announce = announce.replaceAll("&DARK_RED;",	darkred);
		announce = announce.replaceAll("&GOLD;",		gold);
		announce = announce.replaceAll("&GRAY;",		gray);
		announce = announce.replaceAll("&GREEN;",		green);
		announce = announce.replaceAll("&LIGHT_PURPLE;",purple);
		announce = announce.replaceAll("&PURPLE;",		purple);
		announce = announce.replaceAll("&PINK;",		purple);
		announce = announce.replaceAll("&WHITE;", 		white);
		announce = announce.replaceAll("&b;",			aqua);
		announce = announce.replaceAll("&0;",			black);
		announce = announce.replaceAll("&9;",			blue);
		announce = announce.replaceAll("&3;",			darkaqua);
		announce = announce.replaceAll("&1;",			darkblue);
		announce = announce.replaceAll("&8;",			darkgray);
		announce = announce.replaceAll("&2;", 			darkgreen);
		announce = announce.replaceAll("&5;",			darkpurple);
		announce = announce.replaceAll("&4;",			darkred);
		announce = announce.replaceAll("&6;",			gold);
		announce = announce.replaceAll("&7;",			gray);
		announce = announce.replaceAll("&a;",			green);
		announce = announce.replaceAll("&d;",			purple);
		announce = announce.replaceAll("&c;",			red);
		announce = announce.replaceAll("&f;",			white);
		announce = announce.replaceAll("&e;",			yellow);
		announce = announce.replaceAll("&k;",			magic);
		announce = announce.replaceAll("&MAGIC;",		magic);
		announce = announce.replaceAll("&BOLD;",		bold);
		announce = announce.replaceAll("&ITALIC;",		italic);
		announce = announce.replaceAll("&STRIKE;",		strike);
		announce = announce.replaceAll("&UNDERLINE;",	underline);
		announce = announce.replaceAll("&RESET;",		reset);
		return announce;
	}

	protected void broadcastMessage(String s, Player player){
		String announce = " ";
		int _int = Integer.parseInt(s);

		if(_int > strings.size()){
			if(player != null)player.sendMessage(igt+red+"You specified a number that does not correspond to any of the announcements in the file. Remember: it starts at 0! Operation aborted.");
			else warning("You specified an announcement index that does not exist. Remember: it starts at 0!");
		}else{
			try{
				announce = strings.get(_int);
				for(String line: announce.split("&NEW_LINE;")){
					if(tag.equals("") || tag.isEmpty()){
						getServer().broadcastMessage(colourizeText(line));
						info("\""+player+"\""+" has forced an announcement (announcement index: "+_int+").");
						if(player!=null) player.sendMessage(igt+green+"Successfully forced the announcement.");
						else info("Successfully forced the announcement.");
					}else{
						getServer().broadcastMessage(tag+" "+colourizeText(line));
						info("\""+player+"\""+" has forced an announcement (announcement index: "+_int+").");
						if(player!=null) player.sendMessage(igt+green+"Successfully forced the announcement.");
						else info("Successfully forced the announcement.");
					}
				}
			}catch(NumberFormatException e){
				if(player != null) player.sendMessage(red+"Error. No letters or symtbols; only numbers. Try this format: "+darkred+"/fa bc 5 (for more help consult /fa help).");
				else warning("Error. No letters or symbols; only numbers. Try this format: /fa bc 5 (for more help consult /fa help).");
			}
		}
	}
	protected Boolean setupPermissions(){
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null){
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}
	private void setRandom(String[] args, Player player) {
		boolean s = (boolean) Boolean.parseBoolean(args[1]);
		if(s != random){
			random = s;
			ConfigurationHandler.Settings.set("Settings.Random", s);
			if(player != null){
				if(s == true){
					player.sendMessage(igt+green+"Announcer has been successfully changed to announce randomly.");
					info(player.getName()+" has changed FrogAnnounce's live and saved configuration: now announcing randomly.");
				}else{
					player.sendMessage(igt+green+"Announcer has been successfully changed to announce in sequence.");
					info(player.getName()+" has changed FrogAnnounce's live and saved configuration: now announcing in sequence.");
				}
			}else{
				if(s == true)
					info("Announcer has been successfully changed to announce randomly.");
				else
					info("Announcer has been successfully changed to announce in sequence.");
			}
		}else{
			if(player != null){
				if(random == true){
					player.sendMessage(igt+red+"The announcer is already set to announce randomly! There's no need to change it!");
				}else{
					player.sendMessage(igt+red+"The announcer is already set to not announce randomly! There's no need to change it!");
				}
			}else{
				if(random == true)
					info("The announcer is already set to announce randomly! There's no need to change it now!");
				else
					info("The announcer is already set to announce in sequence! There's no need to change it now!");
			}
		}
	}
	private void setInterval(String[] cmdArgs, Player player){
		int newInterval = (int) Integer.parseInt(cmdArgs[1]);
		if(newInterval != interval){
			interval = newInterval;
			ConfigurationHandler.Settings.set("Settings.Interval", interval);
			try {
				ConfigurationHandler.Settings.save(ConfigurationHandler.configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(player != null){
				player.sendMessage(igt+green+"Announcement interval has successfully been changed to "+interval+". Please note that this will "+red+"NOT"+green+" be active until a server/plugin restart/reload.");
				info(player.getName()+" has changed FrogAnnounce's settings: announcement interval changed to "+interval+".");
			}
			else info("Announcement interval has successfully been changed to "+interval+". Please note that this will "+red+"NOT"+green+" be active until a server/plugin restart/reload.");
		}else{
			if(player != null) player.sendMessage(igt+red+"The announcement interval is already set to "+interval+"! There's no need to change it!");
			else info("The announcement interval is already set to "+interval+"! There's no need to change it!");
		}
	}
	public void checkPermissionsVaultPlugins(){
		Plugin vault = this.getServer().getPluginManager().getPlugin("Vault");
		if(vault != null){
			if(vault != null){
				if(setupPermissions()!=null){
					info("Vault hooked successfully.");
					usingPerms = true;
				}else if(setupPermissions() == null){
					info("Vault wasn't found. Defaulting to OP/Non-OP system.");
					usingPerms = false;
				}
			}
		}else{
			warning("Vault is not in your plugins directory! This plugin has a soft dependency of Vault, but if you don't have it, this will still work (you just can't use permission-based stuff).");
		}
	}
	private void ignorePlayer(Player player, String other){
		Player otherPlayer = getServer().getPlayer(other);
		if(other.equals(player.getName()))
			otherPlayer = player;
		else
			otherPlayer = getServer().getPlayer(other);
		if(otherPlayer != null && otherPlayer == player){
			if(permission(player, "frogannounce.ignore", player.isOp())){
				if(!ignoredPlayers.contains(player.getName())){
					ignoredPlayers.add(otherPlayer.getName());
					//				ignoredPlayers.add(++playersIgnoredCounter, player.getName());
					ConfigurationHandler.Settings.set("ignoredPlayers", ignoredPlayers);
					try{
						ConfigurationHandler.Settings.save(ConfigurationHandler.configFile);
						otherPlayer.sendMessage(igt+gray+"You are now being ignored by FrogAnnounce. You will no longer receive announcements from it until you opt back in.");
					}catch (IOException e){
						e.printStackTrace();
					}
				}else{
					player.sendMessage(igt+red+"That player is already being ignored.");
				}
			}else{
				player.sendMessage(igt+red+"You don't have sufficient permission to opt another player out of FrogAnnounce's announcements. Sorry!");
			}
		}else if(otherPlayer != null && otherPlayer != player){
			if(permission(player, "frogannounce.ignore.other", player.isOp())){
				if(!ignoredPlayers.contains(otherPlayer.getName())){
					ignoredPlayers.add(otherPlayer.getName());
					//				ignoredPlayers.add(++playersIgnoredCounter, player.getName());
					ConfigurationHandler.Settings.set("ignoredPlayers", ignoredPlayers);
					try{
						ConfigurationHandler.Settings.save(ConfigurationHandler.configFile);
						player.sendMessage(igt+green+"Success! The player has been added to FrogAnnounce's ignore list and will no longer see its announcements until he/she opts back in.");
						otherPlayer.sendMessage(igt+gray+"You are now being ignored by FrogAnnounce. You will no longer receive announcements from it until you opt back in.");
					}catch (IOException e){
						e.printStackTrace();
					}
				}else{
					player.sendMessage(igt+red+"You're already being ignored by FrogAnnounce.");
				}
			}else{
				player.sendMessage(green+"[FrogAnnounce] "+red+"You don't have sufficient permission to opt another player out of FrogAnnounce's announcements. Sorry!");
			}
		}else{
			player.sendMessage("That player isn't online right now.");
//			if(permission(player, "frogannounce.ignore", player.isOp())){
//				ignoredPlayers.add(player.getName()); 
//				ConfigurationHandler.Settings.set("ignoredPlayers", ignoredPlayers);
//				try{
//					ConfigurationHandler.Settings.save(ConfigurationHandler.configFile);
//				}catch (IOException e){
//					e.printStackTrace();
//				}
//			}
		}
	}
	private void unignorePlayer(Player player, String other){
		Player otherPlayer;
		if(other.isEmpty())
			otherPlayer = player;
		else
			otherPlayer = getServer().getPlayer(other);
		if(otherPlayer != null && otherPlayer == player){
			if(permission(player, "frogannounce.unignore", player.isOp())){
				if(ignoredPlayers.contains(player.getName())){
					ignoredPlayers.remove(otherPlayer.getName());
					ConfigurationHandler.Settings.set("ignoredPlayers", ignoredPlayers);
					try{
						ConfigurationHandler.Settings.save(ConfigurationHandler.configFile);
						otherPlayer.sendMessage(igt+gray+"You are no longer being ignored by FrogAnnounce. You will receive announcements until you opt out of them again.");
					}catch(IOException e){
						e.printStackTrace();
					}
				}else{
					player.sendMessage(igt+red+"You're already not being ignored.");
				}
			}else{
				player.sendMessage(igt+red+"You don't have sufficient permission to opt another player back into FrogAnnounce's announcements. Sorry!");
			}
		}else if(otherPlayer != null && otherPlayer != player){
			if(permission(player, "frogannounce.unignore.other", player.isOp())){
				if(ignoredPlayers.contains(otherPlayer.getName())){
					ignoredPlayers.remove(otherPlayer.getName());
					ConfigurationHandler.Settings.set("ignoredPlayers", ignoredPlayers);
					try{
						ConfigurationHandler.Settings.save(ConfigurationHandler.configFile);
						player.sendMessage(igt+green+"Success! The player has been removed from FrogAnnounce's ignore list and will see its announcements again until he/she opts out again.");
						otherPlayer.sendMessage(igt+gray+"You are no longer being ignored by FrogAnnounce. You will receive announcements until you opt out of them again.");
					}catch(IOException e){
						e.printStackTrace();
					}
				}else{
					player.sendMessage(igt+red+"That player is already not being ignored.");
				}
			}
		}else{
			player.sendMessage(igt+red+"[FrogAnnounce] That player isn't online right now!");
		}
	}
	protected void info(String i){
		logger.log(Level.INFO, pt+i);
	}
	protected void warning(String w){
		logger.log(Level.WARNING, pt+w);
	}
	protected void severe(String s){
		logger.log(Level.SEVERE, pt+s);
	}
	protected void debug(String d){
		if(debugging){
			logger.log(Level.FINE, pt+d);
			getServer().getPlayer("TheLunarFrog").sendMessage(pt+"[DEBUG] "+d);
		}
	}
	class Announcer implements Runnable{
		@Override
		public void run(){
			String announce = "";
			if(random){
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
						if(permission.playerInGroup(p.getWorld().getName(), p.getName(), group) && !ignoredPlayers.contains(p.getName())){
							for (String line : announce.split("&NEW_LINE;")){
								if(ignoredPlayers.contains(p.getName())){
									if(tag.equals("") || tag.equals(" ") || tag.isEmpty()){
										p.sendMessage(colourizeText(line));
									}else{
										p.sendMessage(tag+" "+colourizeText(line));
										//break;
									}
								}
							}
						}
					}
				}
			}else{
				Player[] onlinePlayers = getServer().getOnlinePlayers();
				for(Player p: onlinePlayers){
					for (String line : announce.split("&NEW_LINE;")){
						if(!ignoredPlayers.contains(p.getName())){
							if(tag.equals("") || tag.equals(" ") || tag.isEmpty()){
								p.sendMessage(colourizeText(line));
							}else{
								p.sendMessage(tag+" "+colourizeText(line));
							}
						}
					}
				}
			}
		}
	}
}