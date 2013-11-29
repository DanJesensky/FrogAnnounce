package me.thelunarfrog.frogannounce;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.logging.Logger;

/**
 * Handles the FrogAnnounce configuration.
 *
 * @author Dan | TheLunarFrog
 * @version 1.5.0.0 (Final? Unless more configuration nodes come to be.)
 * @since 1.0.1.3 (Announced as 1.0)
 */
public final class ConfigurationHandler extends FrogAnnounce{
	private YamlConfiguration config = null;
	private final File configFile;

	private static void copyFile(final InputStream fis, final File out) throws Exception{
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(out);
			final byte[] buf = new byte[1024];
			int i = 0;
			if(fis != null){
				while((i = fis.read(buf)) != -1){
					fos.write(buf, 0, i);
				}
				fis.close();
			}
			if(fos != null){
				fos.close();
			}
		}catch(final Exception e){
			throw e;
		}finally{
			if(fos != null){
				fos.close();
			}
		}
	}

	@Override
	public YamlConfiguration getConfig(){
		return this.config = this.loadConfig();
	}

	private void info(final String s){
		Logger.getLogger("Minecraft").info("[FrogAnnounce] " + s);
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
		}else{
			try{
				if(!Bukkit.getServer().getPluginManager().getPlugin("FrogAnnounce").getDataFolder().exists()){
					if(Bukkit.getServer().getPluginManager().getPlugin("FrogAnnounce").getDataFolder().mkdir()){
						this.makeFile();
					}else{
						this.severe("Failed to create configuration folder.");
					}
				}else{
					this.makeFile();
				}
			}catch(final Exception e){
				this.severe("Exception occurred while creating a new configuration file!");
				e.printStackTrace();
			}
		}
		return this.config;
	}

	private void makeFile() throws Exception{
		final InputStream jarURL = ConfigurationHandler.class.getResourceAsStream("/resources/Configuration.yml");
		ConfigurationHandler.copyFile(jarURL, this.configFile);
		this.config = new YamlConfiguration();
		this.config.load(this.configFile);
		this.info("Configuration loaded successfully.");
	}

	@Override
	public void saveConfig(){
		try{
			this.config.save(this.configFile);
		}catch(final IOException e){
			e.printStackTrace();
		}
	}

	private void severe(final String s){
		Logger.getLogger("Minecraft").severe("[FrogAnnounce] " + s);
	}

	protected void updateConfiguration(final String path, final Object newValue){
		if(this.config == null){
			this.config = this.getConfig();
		}
		this.config.set(path, newValue);
		this.saveConfig();
	}

	protected ConfigurationHandler(){
		this.configFile = new File(Bukkit.getServer().getPluginManager().getPlugin("FrogAnnounce").getDataFolder(), "Configuration.yml");
	}
}