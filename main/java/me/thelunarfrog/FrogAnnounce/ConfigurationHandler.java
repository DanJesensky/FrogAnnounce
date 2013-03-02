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
			Settings.save(configFile);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void loadConfig(){
		configFile = new File(Bukkit.getServer().getPluginManager().getPlugin("FrogAnnounce").getDataFolder(), "Configuration.yml");
		if(configFile.exists()){
			Settings = new YamlConfiguration();
			try{
				Settings.load(configFile);
				plugin.interval = Settings.getInt("Settings.Interval", 5);
				plugin.random = Settings.getBoolean("Settings.Random", false);
				plugin.usingPerms = Settings.getBoolean("Settings.Permission", true);
				plugin.strings =(ArrayList)(Settings.getList("Announcer.Strings", new ArrayList<String>()));
				plugin.tag = plugin.colourizeText(Settings.getString("Announcer.Tag", "&GOLD;[FrogAnnounce]"));
				plugin.toGroups = Settings.getBoolean("Announcer.ToGroups", true);
				plugin.Groups =(ArrayList)(Settings.getList("Announcer.Groups", new ArrayList<String>()));
				plugin.ignoredPlayers =(ArrayList<String>) Settings.getList("ignoredPlayers", new ArrayList<String>());
			}catch(FileNotFoundException ex){
				plugin.logger.severe("An exception has occurred while FrogAnnounce was loading the configuration.");
				ex.printStackTrace();
			}catch(IOException ex){
				plugin.logger.severe("An exception has occurred while FrogAnnounce was loading the configuration.");
				ex.printStackTrace();
			}catch(InvalidConfigurationException ex){
				plugin.logger.severe("An exception has occurred while FrogAnnounce was loading the configuration.");
				ex.printStackTrace();
			}
		}else{
			try{
				Bukkit.getServer().getPluginManager().getPlugin("FrogAnnounce").getDataFolder().mkdir();
				InputStream jarURL = ConfigurationHandler.class.getResourceAsStream("/main/resources/Configuration.yml");
				copyFile(jarURL, configFile);
				Settings = new YamlConfiguration();
				Settings.load(configFile);
				plugin.logger.info("Configuration loaded successfully.");
			}catch(Exception e){
				plugin.logger.severe("Exception occurred while creating a new configuration file!");
				e.printStackTrace();
			}
		}
	}

	static private void copyFile(InputStream in, File out) throws Exception{
		InputStream fis = in;
		FileOutputStream fos = new FileOutputStream(out);
		try{
			byte[] buf = new byte[1024];
			int i = 0;
			while((i = fis.read(buf)) != -1){
				fos.write(buf, 0, i);
			}
		}catch(Exception e){
			throw e;
		}finally{
			if(fis != null){
				fis.close();
			}
			if(fos != null){
				fos.close();
			}
		}
	}

	protected ConfigurationHandler(FrogAnnounce plugin){
		this.plugin = plugin;
	}
}