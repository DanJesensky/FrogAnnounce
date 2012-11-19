package main.java.me.thelunarfrog.FrogAnnounce;

import org.bukkit.ChatColor;

public interface ChatColourManager {

	// Colours
	public String aqua 			= ChatColor.AQUA.toString();
	public String black 		= ChatColor.BLACK.toString();
	public String blue 			= ChatColor.BLUE.toString();
	public String darkaqua 		= ChatColor.DARK_AQUA.toString();
	public String darkblue 		= ChatColor.DARK_BLUE.toString();
	public String darkgray 		= ChatColor.DARK_GRAY.toString();
	public String darkgreen 	= ChatColor.DARK_GREEN.toString();
	public String darkpurple 	= ChatColor.DARK_PURPLE.toString();
	public String darkred 		= ChatColor.DARK_RED.toString();
	public String gold 			= ChatColor.GOLD.toString();
	public String gray 			= ChatColor.GRAY.toString();
	public String green 		= ChatColor.GREEN.toString();
	public String purple 		= ChatColor.LIGHT_PURPLE.toString();
	public String red 			= ChatColor.RED.toString();
	public String white 		= ChatColor.WHITE.toString();
	public String yellow 		= ChatColor.YELLOW.toString();
	
	// Magic text from The End credits
	public String magic 		= ChatColor.MAGIC.toString();
	
	// Formatting
	public String bold 			= ChatColor.BOLD.toString();
	public String italic 		= ChatColor.ITALIC.toString();
	public String strike 		= ChatColor.STRIKETHROUGH.toString();
	public String underline 	= ChatColor.UNDERLINE.toString();
	
	// Reset
	public String reset 		= ChatColor.RESET.toString();
	
	//Other stuff
	public String pt			= "[FrogAnnounce] ";
	public String igt			= darkgreen+"[FrogAnnounce] ";
}