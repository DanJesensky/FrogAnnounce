package com.danjesensky.frogannounce.commands;

import com.danjesensky.frogannounce.FrogAnnounce;
import com.danjesensky.frogannounce.infrastructure.announcements.Announcement;
import com.danjesensky.frogannounce.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BroadcastCommand implements CommandExecutor {
    private FrogAnnounce plugin;

    public BroadcastCommand(FrogAnnounce plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 2){
            sender.sendMessage("Usage: ");
        }

        List<Announcement> announcements = this.plugin.getAnnouncer().getAnnouncements();
        for(Announcement announcement: announcements){
            if(announcement.getKey().equalsIgnoreCase(StringUtils.join(1, " ", args))){
                announcement.run();
                return true;
            }
        }

        sender.sendMessage("Couldn't find the specified announcement.");
        return true;
    }
}
