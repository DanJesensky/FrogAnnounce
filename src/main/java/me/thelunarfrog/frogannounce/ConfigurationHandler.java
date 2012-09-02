package main.java.me.thelunarfrog.frogannounce;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigurationHandler extends FrogAnnounce{
	
	protected static YamlConfiguration Settings;
	protected static File configFile;
	private static FrogAnnounce fa = new FrogAnnounce();
	FrogAnnounce plugin;
	
	@Override
	public void saveConfig(){
		try{
			Settings.save(configFile);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	protected static void loadConfig() throws InvalidConfigurationException{
		configFile = new File(Bukkit.getServer().getPluginManager().getPlugin("FrogAnnounce").getDataFolder(), "Configuration.yml");
		if(configFile.exists()){
			Settings = new YamlConfiguration();
			try{
				Settings.load(configFile);
				FrogAnnounce.interval 			= 						Settings.getInt(		"Settings.Interval",	5);
				FrogAnnounce.random			= 							Settings.getBoolean(	"Settings.Random",		false);
				FrogAnnounce.usingPermissionsSystem 	=				Settings.getBoolean(	"Settings.Permission",	true);
				FrogAnnounce.strings 			= (ArrayList)(			Settings.getList(		"Announcer.Strings",	new ArrayList<String>()));
				FrogAnnounce.tag 				= FrogAnnounce.colourizeText(Settings.getString("Announcer.Tag",		"&GOLD;[FrogAnnounce]"));
				FrogAnnounce.usingGroupsSystem	= 						Settings.getBoolean(	"Announcer.ToGroups",	true);
				FrogAnnounce.groups 			= (ArrayList)(			Settings.getList(		"Announcer.Groups",		new ArrayList<String>()));
//				FrogAnnounce.useChatSuite		=						Settings.getBoolean(	"ChatSuite.Use-ChatSuite",false);
				FrogAnnounce.ignoredPlayers		= (ArrayList<String>)	Settings.getList(		"ignoredPlayers",		new ArrayList<String>());
			}catch (FileNotFoundException ex){
				fa.grave("An exception has occurred while FrogAnnounce was loading the configuration.");
				ex.printStackTrace();
			}catch (IOException ex){
				fa.grave("An exception has occurred while FrogAnnounce was loading the configuration.");
				ex.printStackTrace();
			}catch (InvalidConfigurationException ex){
				fa.grave("An exception has occurred while FrogAnnounce was loading the configuration.");
				ex.printStackTrace();
			}
		}else{
			try{
				Bukkit.getServer().getPluginManager().getPlugin("FrogAnnounce").getDataFolder().mkdir();
				InputStream jarURL = ConfigurationHandler.class.getResourceAsStream("/main/resources/Configuration.yml");
				copyFile(jarURL, configFile);
				Settings = new YamlConfiguration();
				Settings.load(configFile);
				fa.conf("Configuration loaded successfully.");
			} catch (Exception e){
				fa.grave("Exception occurred while creating a new configuration file!");
				e.printStackTrace();
			}
		}
	}
	protected static void copyFile(InputStream in, File out) throws Exception{
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = in.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} catch (Exception e){
			throw e;
		} finally {
			if (in != null){
				in.close();
			}
			if (fos != null){
				fos.close();
			}
		}
	}
	protected ConfigurationHandler(){
		this.plugin = FrogAnnounce.plugin;
	}
}