package com.danjesensky.frogannounce.infrastructure.announcements;

import com.danjesensky.frogannounce.FrogAnnounce;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;

//may at some point want to make it so this can be made recurring
public class ManualAnnouncement extends Announcement {
    private FrogAnnounce plugin;
    private CommandSender sender;

    public ManualAnnouncement(FrogAnnounce plugin, CommandSender sender, String text) {
        super(null, plugin.getConfigurationManager().getTag()+text);
        this.plugin = plugin;
        this.sender = sender;
    }

    public void invoke(){
        super.run();
        this.plugin.getLogger().info(sender.getName()+" has invoked a manual broadcast: "+super.getText());
    }
}
