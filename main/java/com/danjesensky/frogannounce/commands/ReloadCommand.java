package com.danjesensky.frogannounce.commands;

import com.danjesensky.frogannounce.FrogAnnounce;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private FrogAnnounce plugin;

    public ReloadCommand(FrogAnnounce plugin){
       this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        plugin.disable();
        plugin.enable();

        return true;
    }
}
