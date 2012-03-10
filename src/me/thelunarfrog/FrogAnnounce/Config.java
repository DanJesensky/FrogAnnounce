package me.thelunarfrog.FrogAnnounce;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
@SuppressWarnings({ "unchecked", "rawtypes" })
public class Config {
	
    protected static YamlConfiguration Settings;
    private static File configFile;
    private static boolean loaded = false;
    
    protected static void saveConfig() throws InvalidConfigurationException{
    	try{
    		FrogAnnounce.Settings.save(configFile);
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
    	catch(Throwable e){
    		e.printStackTrace();
    	}
    }
    public static YamlConfiguration getConfig() throws InvalidConfigurationException {
        if (!loaded) {
            loadConfig();
        }
        return Settings;
    }
    public static File getConfigFile() {
        return configFile;
    }
	public static void loadConfig() throws InvalidConfigurationException{
        configFile = new File(Bukkit.getServer().getPluginManager().getPlugin("FrogAnnounce").getDataFolder(), "Configuration.yml");
        if (configFile.exists()) {
            Settings = new YamlConfiguration();
            try {
                Settings.load(configFile);
        		FrogAnnounce.Interval 			= 			  			Settings.getInt(	"Settings.Interval",	5);
        		FrogAnnounce.isRandom			= 			  			Settings.getBoolean("Settings.Random",		false);
        		FrogAnnounce.permissionConfig 	= 			  			Settings.getBoolean("Settings.Permission",	true);
        		FrogAnnounce.strings 			= (ArrayList)(			Settings.getList(	"Announcer.Strings",	new ArrayList<String>()));
        		FrogAnnounce.Tag 				= FrogAnnounce.colorize(Settings.getString(	"Announcer.Tag", 		"&GOLD;[FrogAnnounce]"));
        		FrogAnnounce.toGroups 			= 			  			Settings.getBoolean("Announcer.ToGroups",	true);
        		FrogAnnounce.Groups 			= (ArrayList)(			Settings.getList(	"Announcer.Groups",		new ArrayList<String>()));
        		FrogAnnounce.ignoredPlayers 	= (ArrayList)(			Settings.getList(	"Announcer.optedOut",	new ArrayList<String>()));
            } catch (FileNotFoundException ex) {
                // TODO: Log
            } catch (IOException ex) {
                // TODO: Log
            } catch (InvalidConfigurationException ex) {
                // TODO: Log
            }
            loaded = true;
        } else {
            try {
                Bukkit.getServer().getPluginManager().getPlugin("FrogAnnounce").getDataFolder().mkdir();
                InputStream jarURL = Config.class.getResourceAsStream("/Configuration.yml");
                copyFile(jarURL, configFile);
                Settings = new YamlConfiguration();
                Settings.load(configFile);
                loaded = true;
                // TODO: Log loaded config
            } catch (Exception e) {
                // TODO: Log
            }
        }
    }
    static private void copyFile(InputStream in, File out) throws Exception {
        InputStream fis = in;
        FileOutputStream fos = new FileOutputStream(out);
        try {
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }
    private Config() {
    }
}