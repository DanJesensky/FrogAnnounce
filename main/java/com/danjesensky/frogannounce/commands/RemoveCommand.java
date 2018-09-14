package com.danjesensky.frogannounce.commands;

import com.danjesensky.frogannounce.FrogAnnounce;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.logging.Level;

public class RemoveCommand implements CommandExecutor {
    private FrogAnnounce plugin;
    private ReloadCommand reload;

    public RemoveCommand(FrogAnnounce plugin, ReloadCommand reload){
        this.plugin = plugin;
        this.reload = reload;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String key = this.plugin.getAnnouncer().getAnnouncements().get(Integer.parseInt(args[1])).getKey();
        this.plugin.getConfigurationManager().setValue("Announcer.Announcements."+key, null);

        try {
            this.plugin.getConfigurationManager().save();
            sender.sendMessage("[FrogAnnounce] Announcement "+key+" was removed successfully.");
        }catch(IOException ex){
            sender.sendMessage("Failed to save configuration: "+ ex.getMessage());
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save configuration. Check permissions on files and directories. ", ex);
        }
        this.reload.onCommand(sender, command, label, args);
        return true;
    }
}
