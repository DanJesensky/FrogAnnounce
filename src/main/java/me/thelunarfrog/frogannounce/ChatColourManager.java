package main.java.me.thelunarfrog.frogannounce;

import org.bukkit.ChatColor;

/**
 * Yeah! The #1 most useless Interface in the world!<br />
 * For easy use of colours. FrogAnnounce implements this interface so that it isn't
 * needed to make a constructor and call it for every string that needs to be coloured.
 * Also easier to use red+"" than just using ChatColor.RED.toString()+"".
 * @author Dan
 * @category ChatColor
 * @version 1.2.8.1
 * @since FrogAnnounce 2.0
 */
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
	//Plugin-specific Stuff
	public String pt			= "[FrogAnnounce] ";
	public String igt			= darkgreen+"[FrogAnnounce] ";
}