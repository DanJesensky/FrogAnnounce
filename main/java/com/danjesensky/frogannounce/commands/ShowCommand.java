package com.danjesensky.frogannounce.commands;

import com.danjesensky.frogannounce.infrastructure.announcements.Announcement;
import com.danjesensky.frogannounce.FrogAnnounce;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShowCommand implements CommandExecutor {
    private FrogAnnounce plugin;

    public ShowCommand(FrogAnnounce plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        sender.sendMessage("Announcements loaded:");
        for(Announcement a: this.plugin.getAnnouncer().getAnnouncements()){
            sender.sendMessage(a.getKey()+": "+a.getText());
        }
        return true;
    }
}
