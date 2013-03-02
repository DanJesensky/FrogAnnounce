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
public class ConfigurationHandler extends FrogAnnounce{
	protected YamlConfiguration Settings;
	protected File configFile;
	FrogAnnounce plugin;

	@Override
	public void saveConfig(){
		try{
			this.Settings.save(this.configFile);
		}catch(final IOException e){
			e.printStackTrace();
		}
	}

	protected void loadConfig(){
		this.configFile = new File(Bukkit.getServer().getPluginManager().getPlugin("FrogAnnounce").getDataFolder(), "Configuration.yml");
		if(this.configFile.exists()){
			this.Settings = new YamlConfiguration();
			try{
				this.Settings.load(this.configFile);
				this.plugin.interval = this.Settings.getInt("Settings.Interval", 5);
				this.plugin.random = this.Settings.getBoolean("Settings.Random", false);
				this.plugin.usingPerms = this.Settings.getBoolean("Settings.Permission", true);
				this.plugin.strings = (ArrayList<String>) this.Settings.getList("Announcer.Strings", new ArrayList<String>());
				this.plugin.tag = this.plugin.colourizeText(this.Settings.getString("Announcer.Tag", "&GOLD;[FrogAnnounce]"));
				this.plugin.toGroups = this.Settings.getBoolean("Announcer.ToGroups", true);
				this.plugin.Groups = (ArrayList<String>) this.Settings.getList("Announcer.Groups", new ArrayList<String>());
				this.plugin.ignoredPlayers = (ArrayList<String>) this.Settings.getList("ignoredPlayers", new ArrayList<String>());
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
				this.Settings = new YamlConfiguration();
				this.Settings.load(this.configFile);
				this.plugin.logger.info("Configuration loaded successfully.");
			}catch(final Exception e){
				this.plugin.logger.severe("Exception occurred while creating a new configuration file!");
				e.printStackTrace();
			}
	}

	private static void copyFile(final InputStream in, final File out) throws Exception{
		final InputStream fis = in;
		final FileOutputStream fos = new FileOutputStream(out);
		try{
			final byte[] buf = new byte[1024];
			int i = 0;
			while((i = fis.read(buf))!=-1)
				fos.write(buf, 0, i);
		}catch(final Exception e){
			throw e;
		}finally{
			if(fis!=null)
				fis.close();
			if(fos!=null)
				fos.close();
		}
	}

	protected ConfigurationHandler(final FrogAnnounce plugin){
		this.plugin = plugin;
	}
}