package main.java.me.thelunarfrog.FrogAnnounce;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Handles the FrogAnnounce configuration.
 * 
 * @version 1.4.0.0 (Final? Unless more configuration nodes come to be.)
 * @category Configuration
 * @since 1.0.1.3 (Announced as 1.0)
 * @author Dan | TheLunarFrog
 */
public final class ConfigurationHandler extends FrogAnnounce{
	protected YamlConfiguration config;
	protected File configFile;
	private final FrogAnnounce plugin;

	@Override
	public void saveConfig(){
		try{
			this.config.save(this.configFile);
		}catch(final IOException e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected void loadConfig(){
		this.configFile = new File(Bukkit.getServer().getPluginManager().getPlugin("FrogAnnounce").getDataFolder(), "Configuration.yml");
		if(this.configFile.exists()){
			this.config = new YamlConfiguration();
			try{
				this.config.load(this.configFile);
				this.plugin.interval = this.config.getInt("Settings.Interval", 5);
				this.plugin.random = this.config.getBoolean("Settings.Random", false);
				this.plugin.usingPerms = this.config.getBoolean("Settings.Permission", true);
				this.plugin.strings = (ArrayList) this.config.getList("Announcer.Strings", new ArrayList<String>());
				this.plugin.tag = this.plugin.colourizeText(this.config.getString("Announcer.Tag", "&GOLD;[FrogAnnounce]"));
				this.plugin.toGroups = this.config.getBoolean("Announcer.ToGroups", true);
				this.plugin.Groups = (ArrayList) this.config.getList("Announcer.Groups", new ArrayList<String>());
				this.plugin.ignoredPlayers = (ArrayList) this.config.getList("ignoredPlayers", new ArrayList<String>());
				this.plugin.showJoinMessage = this.config.getBoolean("Settings.displayMessageOnJoin", false);
				this.plugin.joinMessage = this.config.getString("Announcer.joinMessage", "Welcome to the server! Use /help for assistance.");
				this.plugin.showConsoleAnnouncements = this.config.getBoolean("Settings.showConsoleAnnouncements", false);
			}catch(final FileNotFoundException ex){
				this.plugin.logger.severe("An exception has occurred while FrogAnnounce was loading the configuration.");
				ex.printStackTrace();
			}catch(final IOException ex){
				this.plugin.logger.severe("An exception has occurred while FrogAnnounce was loading the configuration.");
				ex.printStackTrace();
			}catch(final InvalidConfigurationException ex){
				this.plugin.logger.severe("An exception has occurred while FrogAnnounce was loading the configuration.");
				ex.printStackTrace();
			}
		}else
			try{
				Bukkit.getServer().getPluginManager().getPlugin("FrogAnnounce").getDataFolder().mkdir();
				final InputStream jarURL = ConfigurationHandler.class.getResourceAsStream("/main/resources/Configuration.yml");
				ConfigurationHandler.copyFile(jarURL, this.configFile);
				this.config = new YamlConfiguration();
				this.config.load(this.configFile);
				this.plugin.logger.info("Configuration loaded successfully.");
			}catch(final Exception e){
				this.plugin.logger.severe("Exception occurred while creating a new configuration file!");
				e.printStackTrace();
			}
	}

	private static void copyFile(final InputStream fis, final File out) throws Exception{
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(out);
			final byte[] buf = new byte[1024];
			int i = 0;
			while((i = fis.read(buf))!=-1)
				fos.write(buf, 0, i);
			if(fis!=null)
				fis.close();
			if(fos!=null)
				fos.close();
		}catch(final Exception e){
			throw e;
		}finally{
			if(fos!=null)
				fos.close();
		}
	}

	protected ConfigurationHandler(final FrogAnnounce plugin){
		this.plugin = plugin;
	}
}