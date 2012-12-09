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
public class FrogAnnounce extends JavaPlugin implements ChatColourManager{
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
		running = turnOn(null, true);
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
	private boolean turnOn(Player player, boolean startup){
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
			sendMessage(player, 2, darkred+"Announcer is already running.");
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
			if(player!=null)
				running = turnOn(player, true);
			else
				running = turnOn(null, true);
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
						if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?") || args[0].isEmpty() || args == null || args.toString().isEmpty() || args.toString().isEmpty())
							returnHelp(player);
						else if(args[0].equalsIgnoreCase("on"))
							running = turnOn(player, false);
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
						}
						return true;
					}
					catch(ArrayIndexOutOfBoundsException e){
						return false;
					}
				}
				if(permission(player, "frogannounce", player.isOp()) || permission(player, "frogannounce.admin", player.isOp())){
					if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))
						returnHelp(player);
				}
				else{
					sendMessage(player, 1, "You do not have the permission level required to use this command!");
					return true;
				}
			}else if(cmd.getName().equalsIgnoreCase("fa-add")){
				strings.add(args.toString());
				ConfigurationHandler.save();
			}
		}
		return false;
	}
	public void returnHelp(Player player){
		String or = white+"|";
		String auctionStatusColor = darkgreen;
		String helpMainColor = gold;
		String helpCommandColor = aqua;
		String helpObligatoryColor = darkred;
		sendMessage(player, 0, helpMainColor 	+ " * " 			+ auctionStatusColor 	+ "Help for FrogAnnounce 2.1" 			+ helpMainColor	+ " * ");
		sendMessage(player, 0, helpCommandColor+"/fa <help" 		+ or+helpCommandColor+"?>" 		+ helpMainColor 		+ " - Show this message.");
		sendMessage(player, 0, helpCommandColor+"/fa <on" 		+ or+helpCommandColor+"off>" 	+ helpMainColor 		+ " - Start or stop FrogAnnounce.");
		sendMessage(player, 0, helpCommandColor+"/fa <restart" 	+ or+helpCommandColor+"reload>"+helpMainColor 		+ " - Restart FrogAnnounce.");
		sendMessage(player, 0, helpCommandColor+"/fa <interval" 	+ or+helpCommandColor+"int>" 	+ helpObligatoryColor 	+ " <minutes>" 	+ helpMainColor			  +" - Set the time between each announcement.");
		sendMessage(player, 0, helpCommandColor+"/fa <random" 	+ or+helpCommandColor+"rand>"	+ helpObligatoryColor 	+ " <on" 		+ or+helpObligatoryColor+"off>"+helpMainColor+" - Set random or consecutive.");
		sendMessage(player, 0, helpCommandColor+"/fa <broadcast"	+ or+helpCommandColor+"bc>"		+ helpObligatoryColor	+"<AnnouncementIndex>"+helpMainColor+" - Announces the announcement specified by the index immediately. Will not interrupt the normal order/time. Please note that this starts at 0.");
		sendMessage(player, 0, helpCommandColor+"/fa <add "+or+helpCommandColor+"|add> "+helpObligatoryColor+"<announcement message>"+helpMainColor+" - Adds an announcement to the list. (Command /faadd or /fa-add is not a typo; technical restrictions forced this.)");
		sendMessage(player, 0, helpCommandColor+"/fa <remove "+or+"delete"+or+"rem"+or+"del>"+helpObligatoryColor+"<announcementIndex>"+helpMainColor+" - Removes the specified announcement (announcementIndex = announcement number from top to bottom in the file; starts at 0).");
		//sendMessage(helpCommandColor+"/fa <manualbroadcast"+or+helpCommandColor+ "mbc"		+ helpObligatoryColor	+"<Message>"+helpMainColor+" - Announces a message to the entire server. Ignores groups in the config.");
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
					sendMessage(player, 1, "Error. No letters or symtbols; only numbers. Try this format: "+darkred+"/fa bc 5 (for more help consult /fa help).");
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
			sendMessage(player, 0, "Announcement interval has successfully been changed to "+interval+". Please note that this will "+red+"NOT"+green+" be active until a server/plugin restart/reload.");
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
						otherPlayer.sendMessage(igt+gray+"You are now being ignored by FrogAnnounce. You will no longer receive announcements from it until you opt back in.");
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
						otherPlayer.sendMessage(igt+gray+"You are now being ignored by FrogAnnounce. You will no longer receive announcements from it until you opt back in.");
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
						otherPlayer.sendMessage(igt+gray+"You are no longer being ignored by FrogAnnounce. You will receive announcements until you opt out of them again.");
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
						otherPlayer.sendMessage(igt+gray+"You are no longer being ignored by FrogAnnounce. You will receive announcements until you opt out of them again.");
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
				sender.sendMessage(darkgreen+"[FrogAnnounce] "+green+message);
			else if(severity == 1)
				sender.sendMessage(darkgreen+"[FrogAnnounce] "+red+message);
			else if(severity == 2)
				sender.sendMessage(darkgreen+"[FrogAnnounce] "+darkred+message);
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
				player.sendMessage(darkgreen+"[FrogAnnounce] "+green+message);
			else if(severity == 1)
				player.sendMessage(darkgreen+"[FrogAnnounce] "+red+message);
			else if(severity == 2)
				player.sendMessage(darkgreen+"[FrogAnnounce] "+darkred+message);
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
		logger.log(Level.INFO, darkgreen+"[FrogAnnounce] "+green+i);
	}
	protected void warning(String w){
		logger.log(Level.WARNING, darkgreen+"[FrogAnnounce] "+red+w);
	}
	protected void severe(String s){
		logger.log(Level.SEVERE, darkgreen+"[FrogAnnounce]"+darkred+s);
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