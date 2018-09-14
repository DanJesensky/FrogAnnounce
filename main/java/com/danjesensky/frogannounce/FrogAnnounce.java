package com.danjesensky.frogannounce;

import com.danjesensky.frogannounce.commands.BaseCommandHandler;
import com.danjesensky.frogannounce.utils.ConfigurationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrogAnnounce extends JavaPlugin {
    private Logger logger;
    private ConfigurationManager config;
    private Announcer announcer;

    public FrogAnnounce(){
        this.logger = this.getLogger();
    }

    @Override
    public void onEnable(){
        this.enable();

        BaseCommandHandler baseHandler = new BaseCommandHandler(this, this.logger);
        getCommand("fa").setExecutor(baseHandler);
        getCommand("frogannounce").setExecutor(baseHandler);

        logger.log(Level.INFO, "FrogAnnounce "+getDescription().getVersion()+" enabled.");
    }

    public void enable(){
        this.reloadConfig();

        int interval = this.config.getInterval();
        this.announcer = new Announcer(this.config.getAnnouncements(), this, this.config.isRandom());
        super.getServer().getScheduler().scheduleSyncRepeatingTask(this, this.announcer, 1200 *  interval, 1200 * interval);

        for(Announcement a: this.config.getAnnouncements()){
            this.logger.log(Level.INFO, "Loaded announcement: "+a.getText());
        }
    }

    @Override
    public void onDisable() {
        this.disable();
        logger.log(Level.INFO, "FrogAnnounce disabled.");
    }

    public void disable(){
        getServer().getScheduler().cancelTasks(this);
        this.announcer.getAnnouncements().clear();
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return super.onCommand(sender, command, label, args);
    }

    @Override
    public void reloadConfig() {
        try {
            if(this.config == null){
                this.config = new ConfigurationManager(this);
            }else {
                this.config.loadConfig();
            }
        }catch(IOException e){
            this.logger.log(Level.SEVERE, "Unable to access/create configuration file. Ensure your configuration/plugin directories have correct permissions.", e);
        }catch(InvalidConfigurationException e){
            this.logger.log(Level.SEVERE, "FrogAnnounce's configuration is invalid!", e);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return super.onTabComplete(sender, command, alias, args);
    }

    @Override
    public FileConfiguration getConfig() {
        if(this.config == null){
            this.reloadConfig();
        }

        return this.config.getConfig();
    }

    @Override
    public void saveDefaultConfig() {
        super.saveDefaultConfig();
    }

    @Override
    public void saveConfig() {
        super.saveConfig();
    }

    public Logger getLogger(){
        return super.getLogger();
    }

    public Announcer getAnnouncer(){
        return this.announcer;
    }

    public ConfigurationManager getConfigurationManager(){
        return this.config;
    }
}
