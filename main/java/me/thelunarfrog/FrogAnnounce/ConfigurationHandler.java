package main.java.me.thelunarfrog.FrogAnnounce;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Handles the FrogAnnounce configuration.
 * 
 * @version 1.5.0.0 (Final? Unless more configuration nodes come to be.)
 * @category Configuration
 * @since 1.0.1.3 (Announced as 1.0)
 * @author Dan | TheLunarFrog
 */
public final class ConfigurationHandler extends FrogAnnounce{
	private YamlConfiguration config = null;
	private final File configFile;

	@Override
	public void saveConfig(){
		try{
			this.config.save(this.configFile);
		}catch(final IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public YamlConfiguration getConfig(){
		return (this.config = this.loadConfig());
	}

	protected void updateConfiguration(String path, Object newValue){
		if(this.config==null)
			this.config = this.getConfig();
		this.config.set(path, newValue);
		this.saveConfig();
	}

	private YamlConfiguration loadConfig(){
		if(this.configFile.exists()){
			this.config = new YamlConfiguration();
			try{
				this.config.load(this.configFile);
			}catch(final FileNotFoundException ex){
				this.severe("An exception has occurred while FrogAnnounce was loading the configuration.");
				ex.printStackTrace();
			}catch(final IOException ex){
				this.severe("An exception has occurred while FrogAnnounce was loading the configuration.");
				ex.printStackTrace();
			}catch(final InvalidConfigurationException ex){
				this.severe("An exception has occurred while FrogAnnounce was loading the configuration.");
				ex.printStackTrace();
			}
		}else
			try{
				Bukkit.getServer().getPluginManager().getPlugin("FrogAnnounce").getDataFolder().mkdir();
				final InputStream jarURL = ConfigurationHandler.class.getResourceAsStream("/main/resources/Configuration.yml");
				ConfigurationHandler.copyFile(jarURL, this.configFile);
				this.config = new YamlConfiguration();
				this.config.load(this.configFile);
				this.info("Configuration loaded successfully.");
			}catch(final Exception e){
				this.severe("Exception occurred while creating a new configuration file!");
				e.printStackTrace();
			}
		return this.config;
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

	private void severe(String s){
		Logger.getLogger("Minecraft").severe("[FrogAnnounce] "+s);
	}

	private void info(String s){
		Logger.getLogger("Minecraft").info("[FrogAnnounce] "+s);
	}

	protected ConfigurationHandler(final FrogAnnounce plugin){
		this.configFile = new File(Bukkit.getServer().getPluginManager().getPlugin("FrogAnnounce").getDataFolder(), "Configuration.yml");
	}
}