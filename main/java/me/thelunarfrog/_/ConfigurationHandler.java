package me.thelunarfrog._;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Handles the FrogAnnounce configuration.
 *
 * @author Dan | TheLunarFrog
 * @version 1.5.0.0 (Final? Unless more configuration nodes come to be.)
 * @since 1.0.1.3 (Announced as 1.0)
 */
public final class ConfigurationHandler{
	private YamlConfiguration config;

	public ConfigurationHandler(){
		this.config = null;
	}

	private void copyFile(InputStream fis, File out) throws Exception{
		FileOutputStream fos = new FileOutputStream(out);
		try{
			byte[] buffer = new byte[1024];
			int i;
			while((i = fis.read(buffer)) != -1){
				fos.write(buffer, 0, i);
			}
			fis.close();
			fos.close();
		}catch(Exception e){
			fis.close();
			fos.close();
			throw e;
		}
	}

	public YamlConfiguration getConfig() throws Exception{
		if(this.config == null)
			this.loadConfig();
		return this.config;
	}

	public void loadConfig() throws Exception{
		File configFile = new File(Bukkit.getServer().getPluginManager().getPlugin("FrogAnnounce").getDataFolder(), "Configuration.yml");
		if(configFile.exists()){
			this.config = new YamlConfiguration();
			this.config.load(configFile);
		}else{
			if(Bukkit.getPluginManager().getPlugin("FrogAnnounce").getDataFolder().exists() || Bukkit.getServer().getPluginManager().getPlugin("FrogAnnounce").getDataFolder().mkdir()){
				final InputStream jarURL = ConfigurationHandler.class.getResourceAsStream("/Configuration.yml");
				this.copyFile(jarURL, configFile);
				this.config = new YamlConfiguration();
				this.config.load(configFile);
			}else{
				FrogAnnounce.getInstance().sendConsoleMessage(FrogAnnounce.Severity.SEVERE, "Failed to create the directory for configuration.");
			}
		}
	}

	protected void updateConfiguration(final String path, final Object newValue) throws Exception{
		if(this.config == null){
			this.config = this.getConfig();
		}
		this.config.set(path, newValue);
		this.config.save(new File(Bukkit.getServer().getPluginManager().getPlugin("FrogAnnounce").getDataFolder(), "Configuration.yml"));
	}
}