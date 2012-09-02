package main.java.me.thelunarfrog.frogannounce;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class FrogAnnounce extends JavaPlugin implements ChatColourManager{
	Logger log = Logger.getLogger("Minecraft");
	private PluginDescriptionFile pdf;
	protected Permission permission = null;
	protected static List<String> strings, groups;
	protected static int interval;
	protected static boolean random, usingGroupsSystem, usingPermissionsSystem, autoIgnoreOps = false;
	protected String announcementSplitter = "&NEW_LINE;";
	protected static String tag;
	protected boolean running;
	protected int taskID, counter;
	public static FrogAnnounce plugin;
	private static String intervalPath = "Announcer.Interval", stringsPath = "Announcer.Strings", groupsPath = "Announcer.Groups", randomPath = "Announcer.Random", usingGroupsPath = "Announcer.toGroups";
	protected static ArrayList<String> ignoredPlayers = new ArrayList<String>();
	@Override
	public void onEnable(){
		pdf = this.getDescription();
		try {
			ConfigurationHandler.loadConfig();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		setupPermissions();
		startAnnouncer(null);
		String randomlyAnnouncingStartupMessage = random ? "Announcing randomly." : "Announcing in order (non-randomly).";
		conf("Loaded "+strings.size()+" announcements. Announcement interval is set to "+interval+" minute(s). "+randomlyAnnouncingStartupMessage);
		info("Version "+pdf.getVersion()+", created by TheLunarFrog has been enabled.");
	}
	@Override
	public void onDisable(){
		info("Version "+pdf.getVersion()+" has been disabled.");
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		String commandName = cmd.getName();
		Player player = (Player)sender;
		if(commandLabel.equalsIgnoreCase("frogannounce")||commandLabel.equalsIgnoreCase("fa")){
			if((usingPermissionsSystem && permission.has(player, "frogannounce.admin")) || (usingPermissionsSystem && permission.has(player, "frogannounce.*")) || player.isOp() || player == null ){
				if(args[0].isEmpty() || args[0].equals(""))
					help(player);
				if(args[0].equalsIgnoreCase("interval")||args[0].equalsIgnoreCase("int")){
					if(!args[1].contains("-")){
						try{
							int s = (int) Integer.parseInt(args[1]);
							setInterval(player, s);
						}catch(NumberFormatException e){
							sm(player, 2, "Error: that's not an integer. Please use only positive whole numbers and only numbers between 1 and 2147483647, inclusive.");
						}
					}else sm(player, 2, "Error: the number must be positive.");
					return true;
				}else if(args[0].equalsIgnoreCase("random")||args[0].equalsIgnoreCase("rand")){
					boolean t = Boolean.parseBoolean(args[1]);
					if(args[1].equalsIgnoreCase("on"))
						t = true;
					else if(args[1].equalsIgnoreCase("off"))
						t = false;
					setRandom(player, t);
					return true;
				}else if(args[0].equalsIgnoreCase("off")){
					stopAnnouncer(player);
					return true;
				}else if(args[0].equalsIgnoreCase("on")){
					startAnnouncer(player);
					return true;
				}else if(args[0].equalsIgnoreCase("help")){
					help(player);
					return true;
				}else if(args[0].equalsIgnoreCase("reload")||args[0].equalsIgnoreCase("restart")){
					reload(player);
					return true;
				}else if(args[0].equalsIgnoreCase("ignore") || args[0].equalsIgnoreCase("optout") || args[0].equalsIgnoreCase("oo")){
					if(!args[1].isEmpty())
						ignorePlayer(player, args[1]);
					else
						ignorePlayer(player, player.getName());
					return true;
				}else if(args[0].equalsIgnoreCase("unignore") || args[0].equalsIgnoreCase("optin") || args[0].equalsIgnoreCase("oi")){
					if(args[1].isEmpty())
						unignorePlayer(player, args[1]);
					else
						unignorePlayer(player, player.getName());
					return true;
				}else if(args[0].equalsIgnoreCase("remove")){
					if(!args[1].contains("-")){
						try{
							int i = (int) Integer.parseInt(args[1]);
							removeAnnouncement(i, player);
						}catch(NumberFormatException e){
							sm(player, 2, "Error: that's not an integer. Please use only positive whole numbers and only numbers between 1 and 2147483647, inclusive.");
						}
					}else sm(player, 2, "Error: the number must be positive.");
					return true;
				}else return false;
			}else{
				sm(player, 1, "You don't have permission to use that command!");
				return true;
			}
		}else if((commandLabel.equalsIgnoreCase("fa-add")||commandLabel.equalsIgnoreCase("faadd")||commandLabel.equalsIgnoreCase("/add")||commandLabel.equalsIgnoreCase("fadd")||commandLabel.equalsIgnoreCase("addann")) && ((usingPermissionsSystem && permission.has(player, "frogannounce.add")) || (usingPermissionsSystem && permission.has(player, "frogannounce.*")) || player.isOp())){
			if((usingPermissionsSystem && permission.has(player, "frogannounce.*")) || ((usingPermissionsSystem && permission.has(player, "frogannounce.admin"))) || player.isOp()){
				String m = args.toString();
				addAnnouncement(m, player);
				return true;
			}else sm(player, 2, "You don't have permission to use that command!");
			return true;
		}else return false;
	}
	protected boolean startAnnouncer(Player player){
		if(!running){
			if(strings.size() > 0){
				taskID = getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Announcer(), interval*1200, interval*1200);
				if(taskID == -1){
					sm(player, 2, "The announcer module has failed to start! Please check your configuration. If this does not fix it, then submit a support ticket on the BukkitDev page for FrogAnnounce.");
					return false;
				}else{
					counter = 0;
					sm(player, 0, green+"Loaded successfully! Announcing every "+interval+" minute(s).");
					running = true;
					return true;
				}
			}else{
				sm(player, 2, "Announcer has failed to start: there are no announcements!");
				return false;
			}
		}else{
			sm(player, 1, red+"Announcer is already running.");
			return true;
		}
	}
	protected void stopAnnouncer(Player player){
		if(running){
			getServer().getScheduler().cancelTask(taskID);
			sm(player, 0, "Announcements have been disabled successfully!");
			running = false;
		}else{
			if(!running)
				sm(player, 1, red+"No announcements running!"); 
		}
	}
	protected void reload(Player player){
		if(running){
			stopAnnouncer(player);
			try{
				ConfigurationHandler.loadConfig();
			}catch (InvalidConfigurationException e){
				e.printStackTrace();
			}
   			sm(player, 0, darkgreen+"FrogAnnounce has been successfully reloaded!");
			sm(player, -1, darkgreen+"Configuration loaded "+strings.size()+" announcements!");
			running = startAnnouncer(player);
		}else{
			sm(player, 1, red+"No announcements running!");
		}
	}
	protected Boolean setupPermissions(){
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null){
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}
	protected void conf(String c){
		log.log(java.util.logging.Level.CONFIG, pt+c);
	}
	protected void info(String i){
		log.log(java.util.logging.Level.INFO, pt+i);
	}
	protected void warning(String w){
		log.log(java.util.logging.Level.WARNING, pt+w);
	}
	protected void severe(String s){
		log.log(java.util.logging.Level.SEVERE, pt+s);
	}
	protected void grave(String g){
		log.log(java.util.logging.Level.parse("GRAVE"), pt+g);
	}
	public static String colourizeText(String text){
		text = text.replaceAll("&AQUA;",		aqua);
		text = text.replaceAll("&BLACK;",		black);
		text = text.replaceAll("&BLUE;",		blue);
		text = text.replaceAll("&DARK_AQUA;",	darkaqua);
		text = text.replaceAll("&DARK_BLUE;",	darkblue);
		text = text.replaceAll("&DARK_GRAY;",	darkgray);
		text = text.replaceAll("&DARK_GREEN;", 	darkgreen);
		text = text.replaceAll("&DARK_PURPLE;",	darkpurple);
		text = text.replaceAll("&RED;", 		red);
		text = text.replaceAll("&DARK_RED;",	darkred);
		text = text.replaceAll("&GOLD;",		gold);
		text = text.replaceAll("&GRAY;",		gray);
		text = text.replaceAll("&GREEN;",		green);
		text = text.replaceAll("&LIGHT_PURPLE;",purple);
		text = text.replaceAll("&PURPLE;",		purple);
		text = text.replaceAll("&PINK;",		purple);
		text = text.replaceAll("&WHITE;", 		white);
		text = text.replaceAll("&MAGIC;",		magic);
		text = text.replaceAll("&BOLD;",		bold);
		text = text.replaceAll("&ITALIC;",		italic);
		text = text.replaceAll("&STRIKE;",		strike);
		text = text.replaceAll("&UNDERLINE;",	underline);
		text = text.replaceAll("&RESET;",		reset);
		text = text.replaceAll("&a;",			green);
		text = text.replaceAll("&b;",			aqua);
		text = text.replaceAll("&c;",			red);
		text = text.replaceAll("&d;",			purple);
		text = text.replaceAll("&e;",			yellow);
		text = text.replaceAll("&f;",			white);
		text = text.replaceAll("&k;",			magic);
		text = text.replaceAll("&m;",			magic);
		text = text.replaceAll("&b;",			bold);
		text = text.replaceAll("&i;",			italic);
		text = text.replaceAll("&s;",			strike);
		text = text.replaceAll("&u;",			underline);
		text = text.replaceAll("&r;",			reset);
		text = text.replaceAll("&0;",			black);
		text = text.replaceAll("&1;",			darkblue);
		text = text.replaceAll("&2;", 			darkgreen);
		text = text.replaceAll("&3;",			darkaqua);
		text = text.replaceAll("&5;",			darkpurple);
		text = text.replaceAll("&4;",			darkred);
		text = text.replaceAll("&6;",			gold);
		text = text.replaceAll("&7;",			gray);
		text = text.replaceAll("&8;",			darkgray);
		text = text.replaceAll("&9;",			blue);
		return text;
	}
	public void setRandom(Player player, boolean newValue){
		String someGenericString = (random == newValue) ? "FrogAnnounce is already doing that. There's nothing to change!" : "Changed successfully.";
		if(random == newValue){
			sm(player, 1, someGenericString);
		}else{
			random = newValue;
			sm(player, 0, someGenericString);
		}
	}
	public void setInterval(Player player, int newInterval){
		String anotherGenericString = "The announcement delay is already set to "+interval+". There's no need to change it.";
		String genericShit = "Successfully changed announcement interval to "+newInterval+".";
		if(interval == newInterval){
			sm(player, 1, anotherGenericString);
		}else{
			interval = newInterval;
			ConfigurationHandler.Settings.set(intervalPath, newInterval);
			sm(player, -1, genericShit);
		}
	}
	public void help(Player player){
		String or = white+"|"; 
		sm(player, 0, gold + " * " 			+ darkgreen 	+ "Help for FrogAnnounce "+pdf.getVersion() 			+ gold	+ " * ");
		sm(player, 0, aqua + "/fa <help" 		+ or + aqua + "?>" 		+ gold 		+ " - Show this message.");
		sm(player, 0, aqua + "/fa <on" 			+ or + aqua + "off>" 	+ gold 		+ " - Start or stop FrogAnnounce.");
		sm(player, 0, aqua + "/fa <restart" 	+ or + aqua + "reload>" + gold 		+ " - Restart FrogAnnounce.");
		sm(player, 0, aqua + "/fa <interval" 	+ or + aqua + "int>" 	+ darkred 	+ " <minutes>" 	+ gold			   + " - Set the time between each announcement.");
		sm(player, 0, aqua + "/fa <random" 		+ or + aqua + "rand>"	+ darkred 	+ " <on"+ or + darkred + "off>" + gold + " - Set random or consecutive.");
		sm(player, 0, aqua + "/fa <broadcast"	+ or + aqua + "bc>"		+ darkred	+ "<AnnouncementIndex>"	   +	  gold+" - Announces the announcement specified by the index immediately. Will not interrupt the normal order/time. Please note that this starts at 0.");
		//Next two are planned features. They do not work correctly yet.
//		sm(player, 0, aqua + "/fa-add " 		+ or + aqua +"/faadd"	+ darkred	+ "<announcement message>" +	  gold + " - Adds an announcement to the list. (Command /faadd or /fa-add is not a typo; technical restrictions forced this.)");
//		sm(player, 0, aqua + "/fa <remove " 	+ or + aqua + "delete"	+ or +aqua	+ "rem" + or +aqua+ "del>" + darkred + "<announcementIndex>" + gold + " - Removes the specified announcement (announcementIndex = announcement number from top to bottom in the file; starts at 0).");
	}
	public void addAnnouncement(String text, Player p){
		strings.add(text);
		ConfigurationHandler.Settings.set("Announcer.Strings", strings);
		try{
			ConfigurationHandler.Settings.save(ConfigurationHandler.configFile);
			sm(p, 0, "Announcement added successfully.");
		}catch(IOException e){
			sm(p, 3, "Your configuration is invalid! FrogAnnounce cannot save the new settings to the file!");
			e.printStackTrace();
		}
	}
	public void removeAnnouncement(int index, Player p){
		strings.remove(index);
		ConfigurationHandler.Settings.set("Announcer.Strings", strings);
			try{
				ConfigurationHandler.Settings.save(ConfigurationHandler.configFile);
				sm(p, 0, "Announcement removed successfully.");
			}catch (IOException e){
				sm(p, 3, "Your configuration is invalid! FrogAnnounce cannot save the new settings to the file!");
				e.printStackTrace();
			}
	}
	public void ignorePlayer(Player player, String other){
		String p = player.getName();
		Player otherPlayer = getServer().getPlayer(other);
		if(ignoredPlayers.contains(other)){
			String s = (p != other) ? "That player is already being ignored!" : "You are already being ignored!";
			sm(player, 1, s);
		}else{
			ignoredPlayers.add(other);
			
			String q = (p == other) ? "You are now being ignored by FrogAnnounce." : "That player is now being ignored by FrogAnnounce.";
			sm(player, 0, q);
			if(p != other)
				sm(otherPlayer, 0, "You are now being ignored by FrogAnnounce.");
		}
	}
	public void unignorePlayer(Player player, String other){
		String p = player.getName();
		Player otherPlayer = getServer().getPlayer(other);
		if(!ignoredPlayers.contains(other)){
			String s = (p == other) ? "You are already not being ignored!" : "That player is already not being ignored!";
			sm(player, 1, s);
		}else{
			ignoredPlayers.remove(other);
			String q = (p == other) ? "You are no longer being ignored by FrogAnnounce." : "That player is no longer being ignored by FrogAnnounce.";
			sm(player, 0, q);
			if(p != other)
				sm(otherPlayer, 0, "You are no longer being ignored by FrogAnnounce.");
		}
	}
	public void sm(Player p, int severity, String message){
		if(!(p == null)){
			if(severity == -1)
				p.sendMessage(igt+green+message);
			else if(severity == 0)
				p.sendMessage(igt+green+message);
			else if(severity == 1)
				p.sendMessage(igt+reset+message);
			else if(severity == 2)
				p.sendMessage(igt+red+message);
			else if(severity == 3)
				p.sendMessage(igt+darkred+message);
		}else{
			if(severity == -1)
				conf(message);
			else if(severity == 0)
				info(message);
			else if(severity == 1)
				warning(message);
			else if(severity == 2)
				severe(message);
			else if(severity == 3)
				grave(message);
		}
	}
	public void broadcastAnnouncement(int index){
		String announcement = "";
		announcement = strings.get(index);
		Player[] players = getServer().getOnlinePlayers();
		if(usingGroupsSystem){
			for(Player p: players){
				for(String g: groups){
					if(!permission.playerInGroup(p, g)){
						for(String a: announcement.split(announcementSplitter)){
							if((usingPermissionsSystem && !permission.has(p, "frogannounce.autoignore")) || (autoIgnoreOps && !p.isOp())){
								if(tag.isEmpty() || tag.equals("") || tag.equals(" ")){
									if(!(ignoredPlayers.contains(p.getName())))
										p.sendMessage(colourizeText(a));
								}else{
									if(!(ignoredPlayers.contains(p.getName())))
										p.sendMessage(tag+" "+colourizeText(a));
								}
							}
						}
					}
				}
			}
		}else{
			for(Player p: players){
				for(String a: announcement.split(announcementSplitter)){
					if((usingPermissionsSystem && !permission.has(p, "frogannounce.autoignore")) || (autoIgnoreOps && !p.isOp())){
						if(tag.isEmpty()||tag.contains("")||tag.contains(" ")){
							if(!(ignoredPlayers.contains(p.getName()))){
								p.sendMessage(colourizeText(a));
							}
						}else{
							if(!(ignoredPlayers.contains(p.getName()))){
								p.sendMessage(tag+" "+colourizeText(a));
							}
						}
					}
				}
			}
		}
	}
	class Announcer implements Runnable {
		@Override
		public void run(){
			if(counter > strings.size())
				counter = 0;
			Random r = new Random();
			String announcement = random ? strings.get(r.nextInt()) : strings.get(counter);
			counter++;
			Player[] players = getServer().getOnlinePlayers();
			if(usingGroupsSystem){
				for(Player p: players){
					for(String g: groups){
						if(!permission.playerInGroup(p, g)){
							for(String a: announcement.split(announcementSplitter)){
								if((usingPermissionsSystem && !permission.has(p, "frogannounce.autoignore")) || (autoIgnoreOps && !p.isOp())){
									if(!(ignoredPlayers.contains(p.getName()))){
										if(tag.isEmpty()||tag.equals("")||tag.equals(" "))
											p.sendMessage(colourizeText(a));
										else
											p.sendMessage(tag+" "+colourizeText(a));
									}
								}
							}
						}
					}
				}
			}else{
				for(Player p: players){
					for(String a: announcement.split(announcementSplitter)){
						if((usingPermissionsSystem && !permission.has(p, "frogannounce.autoignore")) || (autoIgnoreOps && !p.isOp())){
							if(!(ignoredPlayers.contains(p.getName()))){
								if(tag.isEmpty()||tag.equals("")||tag.equals(" ")){
									p.sendMessage(colourizeText(a));
								}else{
									p.sendMessage(tag+colourizeText(a));
								}
							}
						}
					}
				}
			}
		}
	}
}