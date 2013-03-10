package main.java.me.thelunarfrog.FrogAnnounce;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;

public class FrogLog{
	protected void info(final String i){
		Logger.getLogger("Minecraft").log(Level.INFO, "[FrogAnnounce] "+ChatColor.stripColor(i));
	}

	protected void warning(final String w){
		Logger.getLogger("Minecraft").log(Level.WARNING, "[FrogAnnounce] "+ChatColor.stripColor(w));
	}

	protected void severe(final String s){
		Logger.getLogger("Minecraft").log(Level.SEVERE, "[FrogAnnounce] "+ChatColor.stripColor(s));
	}
}