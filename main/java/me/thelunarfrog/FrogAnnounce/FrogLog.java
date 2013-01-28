package main.java.me.thelunarfrog.FrogAnnounce;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;

public class FrogLog{
	protected void info(String i){
		Logger.getLogger("Minecraft").log(Level.INFO, ChatColor.DARK_GREEN+"[FrogAnnounce] "+ChatColor.GREEN+i);
	}
	protected void warning(String w){
		Logger.getLogger("Minecraft").log(Level.WARNING, ChatColor.DARK_GREEN+"[FrogAnnounce] "+ChatColor.RED+w);
	}
	protected void severe(String s){
		Logger.getLogger("Minecraft").log(Level.SEVERE, ChatColor.DARK_GREEN+"[FrogAnnounce] "+ChatColor.DARK_RED+s);
	}
}