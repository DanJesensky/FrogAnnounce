package com.danjesensky.frogannounce.commands;

import com.danjesensky.frogannounce.infrastructure.announcements.Announcement;
import com.danjesensky.frogannounce.FrogAnnounce;
import com.danjesensky.frogannounce.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class AddCommand implements CommandExecutor {
    private FrogAnnounce plugin;
    private ReloadCommand reload;

    public AddCommand(FrogAnnounce plugin, ReloadCommand reload){
        this.plugin = plugin;
        this.reload = reload;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String key;
        List<Announcement> announcements = this.plugin.getConfigurationManager().getAnnouncements();
        int index = -1;

        List<String> keys = new LinkedList<>();
        for(Announcement a: announcements){
            keys.add(a.getKey());
        }

        while(keys.contains(String.valueOf(++index)));

        key = "Announcer.Announcements." +index;

        this.plugin.getConfigurationManager().setValue(key+".Interval", -1);
        this.plugin.getConfigurationManager().setValue(key+".Text", StringUtils.join(1, " ", args));
        try {
            this.plugin.getConfigurationManager().save();
            sender.sendMessage("[FrogAnnounce] Announcement "+key+" was added successfully.");
        }catch(IOException ex){
            sender.sendMessage("Failed to save configuration: "+ ex.getMessage());
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save configuration. Check permissions on files and directories. ", ex);
        }
        this.reload.onCommand(sender, command, label, args);
        return true;
    }
}
