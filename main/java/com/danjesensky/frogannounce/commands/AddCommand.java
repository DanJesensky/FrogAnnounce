package com.danjesensky.frogannounce.commands;

import com.danjesensky.frogannounce.Announcement;
import com.danjesensky.frogannounce.FrogAnnounce;
import com.danjesensky.frogannounce.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.logging.Logger;

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
        while(announcements.contains((++index)+"")) plugin.getLogger().info("index: "+index);
        key = "Announcer.Announcements." +index;

        this.plugin.getConfigurationManager().setValue(key+".Interval", -1);
        this.plugin.getConfigurationManager().setValue(key+".Text", StringUtils.join(1, " ", args));
        try {
            this.plugin.getConfigurationManager().save();
        }catch(Exception ex){}
        this.reload.onCommand(sender, command, label, args);
        return true;
    }
}
