package com.danjesensky.frogannounce.utils;

import com.danjesensky.frogannounce.infrastructure.announcements.Announcement;
import com.danjesensky.frogannounce.infrastructure.announcements.IndependentAnnouncement;
import com.danjesensky.frogannounce.infrastructure.announcements.QueuedAnnouncement;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class ConfigurationManager {
    private YamlConfiguration config;
    private final File dataDir;
    private final File configFile;

    public ConfigurationManager(JavaPlugin plugin) throws IOException, InvalidConfigurationException {
        this.dataDir = plugin.getDataFolder();
        this.configFile = new File(dataDir, "config.yml");
        this.loadConfig();
    }

    public YamlConfiguration getConfig(){
        return this.config;
    }

    public void loadConfig() throws IOException, InvalidConfigurationException {
        if (!configFile.exists()) {
            this.saveDefaultConfig();
        }

        this.config = new YamlConfiguration();
        this.config.load(configFile);
    }

    public void saveDefaultConfig() throws IOException {
        if (!dataDir.exists() && !dataDir.mkdir()) {
            throw new IOException("Failed to create the directory for configuration.");
        }
        try (final InputStream fis = ConfigurationManager.class.getResourceAsStream("/resources/config.yml");
             final FileOutputStream fos = new FileOutputStream(configFile)) {

            byte[] buffer = new byte[1024];
            int read;

            while ((read = fis.read(buffer, 0, buffer.length)) != -1) {
                fos.write(buffer, 0, read);
            }
        }
    }

    public void setValue(String key, Object value){
        this.config.set(key, value);
    }

    public void save() throws IOException {
        this.config.save(this.configFile);
    }

    public Announcement getAnnouncement(String index){
        String key = "Announcer.Announcements."+index;
        if(!this.config.getBoolean(key+".Enabled", true)){
            return null;
        }

        Announcement a;
        String text;
        int interval = this.config.getInt(key+".Interval");
        text = this.config.getString("Announcer.Announcements."+index+".Text");

        if(this.config.getBoolean(key+".Tag", true)){
            text = this.getTag() + text;
        }

        if(interval <= 0){
            a = new QueuedAnnouncement(index, text);
        }else{
            a = new IndependentAnnouncement(index, text, interval);
        }

        return a;
    }

    public List<Announcement> getAnnouncements(){
        List<Announcement> announcements = new LinkedList<>();

        for(String key: this.config.getConfigurationSection("Announcer.Announcements").getKeys(false)){
            announcements.add(this.getAnnouncement(key));
        }

        return announcements;
    }

    public int getInterval(){
        return this.config.getInt("Settings.Interval", 5);
    }

    public boolean isRandom(){
        return this.config.getBoolean("Settings.Random", false);
    }

    public String getTag(){
        return StringUtils.recolorText(this.config.getString("Announcer.Tag", "&DARK_GREEN;[FrogAnnounce]"));
    }
}
