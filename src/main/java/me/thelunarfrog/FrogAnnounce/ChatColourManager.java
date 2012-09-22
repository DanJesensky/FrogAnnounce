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
	public final String aqua 			= ChatColor.AQUA.toString();
	public final String black 		= ChatColor.BLACK.toString();
	public final String blue 			= ChatColor.BLUE.toString();
	public final String darkaqua 		= ChatColor.DARK_AQUA.toString();
	public final String darkblue 		= ChatColor.DARK_BLUE.toString();
	public final String darkgray 		= ChatColor.DARK_GRAY.toString();
	public final String darkgreen 	= ChatColor.DARK_GREEN.toString();
	public final String darkpurple 	= ChatColor.DARK_PURPLE.toString();
	public final String darkred 		= ChatColor.DARK_RED.toString();
	public final String gold 			= ChatColor.GOLD.toString();
	public final String gray 			= ChatColor.GRAY.toString();
	public final String green 		= ChatColor.GREEN.toString();
	public final String purple 		= ChatColor.LIGHT_PURPLE.toString();
	public final String red 			= ChatColor.RED.toString();
	public final String white 		= ChatColor.WHITE.toString();
	public final String yellow 		= ChatColor.YELLOW.toString();
	// Magic text from The End credits
	public final String magic 		= ChatColor.MAGIC.toString();
	// Formatting
	public final String bold 			= ChatColor.BOLD.toString();
	public final String italic 		= ChatColor.ITALIC.toString();
	public final String strike 		= ChatColor.STRIKETHROUGH.toString();
	public final String underline 	= ChatColor.UNDERLINE.toString();
	// Reset
	public final String reset 		= ChatColor.RESET.toString();
	//Plugin-specific Stuff
	public final String pt			= "[FrogAnnounce] ";
	public final String igt			= darkgreen+"[FrogAnnounce] ";
}