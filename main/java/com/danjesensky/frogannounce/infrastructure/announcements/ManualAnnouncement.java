package com.danjesensky.frogannounce.infrastructure.announcements;

import com.danjesensky.frogannounce.FrogAnnounce;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;

//may at some point want to make it so this can be made recurring
public class ManualAnnouncement extends Announcement {
    private Logger logger;
    private CommandSender sender;

    public ManualAnnouncement(CommandSender sender, Logger logger, String text) {
        super(null, text);
        this.logger = logger;
        this.sender = sender;
    }

    public void invoke(){
        super.invoke();
        this.logger.info(sender.getName()+" has invoked a manual broadcast: "+super.getText());
    }
}
