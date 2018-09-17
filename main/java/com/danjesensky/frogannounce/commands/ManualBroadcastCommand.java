package com.danjesensky.frogannounce.commands;

import com.danjesensky.frogannounce.FrogAnnounce;
import com.danjesensky.frogannounce.infrastructure.announcements.IndependentAnnouncement;
import com.danjesensky.frogannounce.infrastructure.announcements.ManualAnnouncement;
import com.danjesensky.frogannounce.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ManualBroadcastCommand implements CommandExecutor {
    private FrogAnnounce plugin;

    public ManualBroadcastCommand(FrogAnnounce plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        new ManualAnnouncement(sender, this.plugin.getLogger(), StringUtils.join(1, " ", args)).invoke();
        return true;
    }
}
