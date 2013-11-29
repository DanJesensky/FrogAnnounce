package me.thelunarfrog.frogannounce;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;

public class FrogLog{
	private final Logger	logger;

	protected void info(final String i){
		this.logger.log(Level.INFO, "[FrogAnnounce] " + ChatColor.stripColor(i));
	}

	protected void severe(final String s){
		this.logger.log(Level.SEVERE, "[FrogAnnounce] " + ChatColor.stripColor(s));
	}

	protected void warning(final String w){
		this.logger.log(Level.WARNING, "[FrogAnnounce] " + ChatColor.stripColor(w));
	}

	public FrogLog(){
		this.logger = Logger.getLogger("Minecraft");
	}
}