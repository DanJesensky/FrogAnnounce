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
public class FrogAnnounce extends JavaPlugin{
	private PluginDescriptionFile pdfFile;
	private Logger logger = Logger.getLogger("Minecraft");
	public static Permission permission = null;
	protected static String tag;
	protected static int interval, taskId = -1, counter = 0, playersIgnoredCounter, permissionsSystem;
	protected static boolean running = false, random, permissionsEnabled = false, toGroups, permissionConfig, usingPerms;
	protected static List<String> strings, Groups;
	protected static ArrayList<String> ignoredPlayers = null;
	public static FrogAnnounce plugin;

	@Override
	public void onEnable(){
		plugin = this;
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
		running = turnOn(null);
		info("Version "+pdfFile.getVersion()+" by TheLunarFrog has been enabled!");
	}
	@Override
	public void onDisable(){
		turnOff(true, null);
		info("Version "+pdfFile.getVersion()+" by TheLunarFrog has been disabled!");
	}
	private boolean permission(Player player, String line, Boolean op){
		if(permissionsEnabled){
			return permission.has(player, line);
		}else{
			return op;
		}
	}
	private void turnOff(boolean disabled, Player player){
		if(running){
			getServer().getScheduler().cancelTask(taskId);
			sendMessage(player, 0, "Announcer disabled!");
			running = false;
		}else{
			if(!disabled)
				sendMessage(player, 2, "The announcer is not running!");
		}
	}
	private boolean turnOn(Player player){
		if(!running){
			if(strings.size() > 0){
				taskId = getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Announcer(), interval * 1200, interval * 1200);
				if(taskId==-1){
					sendMessage(player, 2, "The announcer module has failed to start! Please check your configuration. If this does not fix it, then submit a support ticket on the BukkitDev page for FrogAnnounce.");
					return false;
				}else{
					counter = 0;
					sendMessage(player, 0, "Success! Now announcing every "+ interval +" minute(s)!");
					return true;
				}
			}else{
				sendMessage(player, 2, "The announcer failed to start! There are no announcements!");
				return false;
			}
		}else{
			sendMessage(player, 2, ChatColor.DARK_RED+"Announcer is already running.");
			return true;
		}
	}
	private void reloadPlugin(Player player){
		if(running){
			turnOff(false, null);
			try{
				ConfigurationHandler.loadConfig();
			}catch(InvalidConfigurationException e){
				e.printStackTrace();
			}
			running = turnOn(player);
			sendMessage(player, 0, "FrogAnnounce has been successfully reloaded!");
			sendMessage(player, 0, "Settings loaded "+strings.size()+" announcements!");
		}else{
			sendMessage(player, 2, "No announcements running!");
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		String commandName = cmd.getName();
		if(sender instanceof Player){
			Player player=(Player)sender;
			if(commandLabel.equalsIgnoreCase("fa") || commandLabel.equalsIgnoreCase("frogannounce")){
				if(permission(player, "frogannounce.admin", player.isOp()) || permission(player, "frogannounce.*", player.isOp()) || permission(player, "frogannounce.command."+commandName.toLowerCase(), player.isOp())){
					try{
						if(args.length == 0){
							sendMessage(sender, 0, "FrogAnnounce version: "+pdfFile.getVersion());
							sendMessage(sender, 0, "For help, use /fa help.");
						}else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?") || args[0].isEmpty() || args == null || args.toString().isEmpty() || args.toString().isEmpty())
							returnHelp(player);
						else if(args[0].equalsIgnoreCase("on"))
							running = turnOn(player);
						else if(args[0].equalsIgnoreCase("off"))
							turnOff(false, player);
						else if(args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("v"))
							sendMessage(sender, 0, "Current version: "+pdfFile.getVersion());
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
							sendMessage(sender, 0, "Loaded announcements:");
							for(String s: strings)
								sendMessage(sender, 0, colourizeText(s));
						}else if(args[0].equalsIgnoreCase("add")){
							StringBuilder sb = new StringBuilder();
							for(int i = 1; i < args.length; i++){
								sb.append(args[i]+" ");
							}
							strings.add(sb.toString().trim());
							ConfigurationHandler.Settings.set("Announcer.Strings", strings);
							ConfigurationHandler.save();
						}else{
							sendMessage(sender, 1, "That didn't seem like a valid command. Here's some help...");
							returnHelp(sender);
						}
						return true;
					}
					catch(ArrayIndexOutOfBoundsException e){
						return false;
					}
				}else if(args[0].equalsIgnoreCase("ignore") || args[0].equalsIgnoreCase("optout") || args[0].equalsIgnoreCase("opt-out")){
					if(permission(player, "frogannounce.optout", player.isOp()))
						ignorePlayer(player, args[1]);
					else
						sendMessage(sender, 1, "You don't have permission to access that command.");
				}else if(args[0].equalsIgnoreCase("unignore") || args[0].equalsIgnoreCase("optin") || args[0].equalsIgnoreCase("opt-in")){
					if(permission(player, "frogannounce.optin", player.isOp()))
						ignorePlayer(player, args[1]);
					else
						sendMessage(sender, 1, "You don't have permission to access that command.");
				}
			}
		}
		return false;
	}
	public void returnHelp(CommandSender sender){
		String or = ChatColor.WHITE.toString()+"|";
		String auctionStatusColor = ChatColor.DARK_GREEN.toString();
		String helpMainColor = ChatColor.GOLD.toString();
		String helpCommandColor = ChatColor.AQUA.toString();
		String helpObligatoryColor = ChatColor.DARK_RED.toString();
		sendMessage(sender, 0, helpMainColor 	+ " * " 			+ auctionStatusColor 	+ "Help for FrogAnnounce 2.1" 			+ helpMainColor	+ " * ");
		sendMessage(sender, 0, helpCommandColor+"/fa <help" 		+ or+helpCommandColor+"?>" 		+ helpMainColor 		+ " - Show this message.");
		sendMessage(sender, 0, helpCommandColor+"/fa <on" 		+ or+helpCommandColor+"off>" 	+ helpMainColor 		+ " - Start or stop FrogAnnounce.");
		sendMessage(sender, 0, helpCommandColor+"/fa <restart" 	+ or+helpCommandColor+"reload>"+helpMainColor 		+ " - Restart FrogAnnounce.");
		sendMessage(sender, 0, helpCommandColor+"/fa <interval" 	+ or+helpCommandColor+"int>" 	+ helpObligatoryColor 	+ " <minutes>" 	+ helpMainColor			  +" - Set the time between each announcement.");
		sendMessage(sender, 0, helpCommandColor+"/fa <random" 	+ or+helpCommandColor+"rand>"	+ helpObligatoryColor 	+ " <on" 		+ or+helpObligatoryColor+"off>"+helpMainColor+" - Set random or consecutive.");
		sendMessage(sender, 0, helpCommandColor+"/fa <broadcast"	+ or+helpCommandColor+"bc>"		+ helpObligatoryColor	+"<AnnouncementIndex>"+helpMainColor+" - Announces the announcement specified by the index immediately. Will not interrupt the normal order/time. Please note that this starts at 0.");
		sendMessage(sender, 0, helpCommandColor+"/fa <add "+or+helpCommandColor+"|add> "+helpObligatoryColor+"<announcement message>"+helpMainColor+" - Adds an announcement to the list. (Command /faadd or /fa-add is not a typo; technical restrictions forced this.)");
		sendMessage(sender, 0, helpCommandColor+"/fa <remove "+or+"delete"+or+"rem"+or+"del>"+helpObligatoryColor+"<announcementIndex>"+helpMainColor+" - Removes the specified announcement (announcementIndex = announcement number from top to bottom in the file; starts at 0).");
		//sendMessage(helpCommandColor+"/fa <manualbroadcast"+or+helpCommandColor+ "mbc"		+ helpObligatoryColor	+"<Message>"+helpMainColor+" - Announces a message to the entire server. Ignores groups in the config.");
	}
	protected static String colourizeText(String announce){
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
		announce = announce.replaceAll("&PURPLE;",		ChatColor.LIGHT_PURPLE.toString());
		announce = announce.replaceAll("&PINK;",		ChatColor.LIGHT_PURPLE.toString());
		announce = announce.replaceAll("&WHITE;", 		ChatColor.WHITE.toString());
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
		announce = announce.replaceAll("&k;",			ChatColor.MAGIC.toString());
		announce = announce.replaceAll("&MAGIC;",		ChatColor.MAGIC.toString());
		announce = announce.replaceAll("&BOLD;",		ChatColor.BOLD.toString());
		announce = announce.replaceAll("&ITALIC;",		ChatColor.ITALIC.toString());
		announce = announce.replaceAll("&STRIKE;",		ChatColor.STRIKETHROUGH.toString());
		announce = announce.replaceAll("&UNDERLINE;",	ChatColor.UNDERLINE.toString());
		announce = announce.replaceAll("&RESET;",		ChatColor.RESET.toString());
		return announce;
	}

	protected void broadcastMessage(String s, Player player){
		String announce = null;
		int _int = 0;
		try{
			Integer.parseInt(s);
			if(_int > strings.size()){
				sendMessage(player, 1, "You specified a number that does not correspond to any of the announcements in the file. Remember: it starts at 0! Operation aborted.");
			}else{
				try{
					announce = strings.get(_int);
					for(String line: announce.split("&NEW_LINE;")){
						if(tag.equals("") || tag.isEmpty()){
							getServer().broadcastMessage(colourizeText(line));
							sendMessage(player, 0, "Successfully forced the announcement.");
						}else{
							getServer().broadcastMessage(tag+" "+colourizeText(line));
							sendMessage(player, 0, "Successfully forced the announcement.");
						}
					}
				}catch(NumberFormatException e){
					sendMessage(player, 1, "Error. No letters or symtbols; only numbers. Try this format: "+ChatColor.DARK_RED+"/fa bc 5 (for more help consult /fa help).");
				}
			}
		}catch(NumberFormatException e){
			sendMessage(player, 1, "Only numbers can be entered as an index. Remember to start counting at 0.");
		}
	}
	protected Boolean setupPermissions(){
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null){
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}
	private void setRandom(String[] args, Player player){
		boolean s = (boolean) Boolean.parseBoolean(args[1]);
		if(s != random){
			random = s;
			ConfigurationHandler.Settings.set("Settings.Random", s);
			if(s==true)
				sendMessage(player, 0, "Announcer has been successfully changed to announce randomly.");
			else
				sendMessage(player, 0, "Announcer has been successfully changed to announce in sequence.");
			ConfigurationHandler.save();
		}else{
			if(random == true)
				sendMessage(player, 1, "The announcer is already set to announce randomly! There's no need to change it!");
			else
				sendMessage(player, 1, "The announcer is already set to not announce randomly! There's no need to change it!");
		}
	}
	private void setInterval(String[] cmdArgs, Player player){
		int newInterval = (int) Integer.parseInt(cmdArgs[1]);
		if(newInterval != interval){
			interval = newInterval;
			ConfigurationHandler.Settings.set("Settings.Interval", interval);
			ConfigurationHandler.save();
			sendMessage(player, 0, "Announcement interval has successfully been changed to "+interval+". Please note that this will "+ChatColor.RED+"NOT"+ChatColor.GREEN+" be active until a server/plugin restart/reload.");
		}else{
			sendMessage(player, 1, "The announcement interval is already set to "+interval+"! There's no need to change it!");
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
						sendMessage(otherPlayer, 0, ChatColor.GRAY+"You are now being ignored by FrogAnnounce. You will no longer receive announcements from it until you opt back in.");
					}catch (IOException e){
						e.printStackTrace();
					}
				}else{
					sendMessage(player, 1, "That player is already being ignored.");
				}
			}else{
				sendMessage(player, 1, "You don't have sufficient permission to opt another player out of FrogAnnounce's announcements. Sorry!");
			}
		}else if(otherPlayer!=null && otherPlayer!=player){
			if(permission(player, "frogannounce.ignore.other", player.isOp())){
				if(!ignoredPlayers.contains(otherPlayer.getName())){
					ignoredPlayers.add(otherPlayer.getName());
					//ignoredPlayers.add(++playersIgnoredCounter, player.getName());
					ConfigurationHandler.Settings.set("ignoredPlayers", ignoredPlayers);
					try{
						ConfigurationHandler.Settings.save(ConfigurationHandler.configFile);
						sendMessage(player, 0, "Success! The player has been added to FrogAnnounce's ignore list and will no longer see its announcements until he/she opts back in.");
						sendMessage(otherPlayer, 0, ChatColor.GRAY+"You are now being ignored by FrogAnnounce. You will no longer receive announcements from it until you opt back in.");
					}catch (IOException e){
						e.printStackTrace();
					}
				}else{
					sendMessage(player, 1, "You're already being ignored by FrogAnnounce.");
				}
			}else{
				sendMessage(player, 1, "You don't have sufficient permission to opt another player out of FrogAnnounce's announcements. Sorry!");
			}
		}else{
			sendMessage(player, 1, "That player isn't online right now.");
			//if(permission(player, "frogannounce.ignore", player.isOp())){
			//	ignoredPlayers.add(player.getName()); 
			//	ConfigurationHandler.Settings.set("ignoredPlayers", ignoredPlayers);
			//	try{
			//		ConfigurationHandler.Settings.save(ConfigurationHandler.configFile);
			//	}catch (IOException e){
			//		e.printStackTrace();
			//	}
			//}
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
						sendMessage(otherPlayer, 0, ChatColor.GRAY+"You are no longer being ignored by FrogAnnounce. You will receive announcements until you opt out of them again.");
					}catch(IOException e){
						e.printStackTrace();
					}
				}else{
					sendMessage(player, 1, "You're already not being ignored.");
				}
			}else{
				sendMessage(player, 1, "You don't have sufficient permission to opt another player back into FrogAnnounce's announcements. Sorry!");
			}
		}else if(otherPlayer != null && otherPlayer != player){
			if(permission(player, "frogannounce.unignore.other", player.isOp())){
				if(ignoredPlayers.contains(otherPlayer.getName())){
					ignoredPlayers.remove(otherPlayer.getName());
					ConfigurationHandler.Settings.set("ignoredPlayers", ignoredPlayers);
					try{
						ConfigurationHandler.Settings.save(ConfigurationHandler.configFile);
						sendMessage(player, 0, "Success! The player has been removed from FrogAnnounce's ignore list and will see its announcements again until he/she opts out again.");
						sendMessage(otherPlayer, 0, ChatColor.GRAY+"You are no longer being ignored by FrogAnnounce. You will receive announcements until you opt out of them again.");
					}catch(IOException e){
						e.printStackTrace();
					}
				}else{
					sendMessage(player, 1, "That player is already not being ignored.");
				}
			}
		}else{
			sendMessage(player, 1, "That player isn't online right now!");
		}
	}
	protected void sendMessage(CommandSender sender, int severity, String message){
		if(sender instanceof Player){
			if(severity == 0)
				sender.sendMessage(ChatColor.DARK_GREEN+"[FrogAnnounce] "+ChatColor.GREEN+message);
			else if(severity == 1)
				sender.sendMessage(ChatColor.DARK_GREEN+"[FrogAnnounce] "+ChatColor.RED+message);
			else if(severity == 2)
				sender.sendMessage(ChatColor.DARK_GREEN+"[FrogAnnounce] "+ChatColor.DARK_RED+message);
		}else{
			if(severity == 0)
				info(message);
			else if(severity == 1)
				warning(message);
			else if(severity == 2)
				severe(message);
		}
	}
	protected void sendMessage(Player player, int severity, String message){
		if(player != null){
			if(severity == 0)
				player.sendMessage(ChatColor.DARK_GREEN+"[FrogAnnounce] "+ChatColor.GREEN+message);
			else if(severity == 1)
				player.sendMessage(ChatColor.DARK_GREEN+"[FrogAnnounce] "+ChatColor.RED+message);
			else if(severity == 2)
				player.sendMessage(ChatColor.DARK_GREEN+"[FrogAnnounce] "+ChatColor.DARK_RED+message);
		}else{
			if(severity == 0)
				info(message);
			else if(severity == 1)
				warning(message);
			else if(severity == 2)
				severe(message);
		}
	}
	protected void info(String i){
		logger.log(Level.INFO, ChatColor.DARK_GREEN+"[FrogAnnounce] "+ChatColor.GREEN+i);
	}
	protected void warning(String w){
		logger.log(Level.WARNING, ChatColor.DARK_GREEN+"[FrogAnnounce] "+ChatColor.RED+w);
	}
	protected void severe(String s){
		logger.log(Level.SEVERE, ChatColor.DARK_GREEN+"[FrogAnnounce]"+ChatColor.DARK_RED+s);
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