package com.danjesensky.frogannounce.commands;

import com.danjesensky.frogannounce.FrogAnnounce;
import com.danjesensky.frogannounce.exceptions.InsufficientPermissionsException;
import com.danjesensky.frogannounce.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseCommandHandler implements CommandExecutor {
    private Logger logger;
    private ReloadCommand reload;
    private EnableCommand enable;
    private DisableCommand disable;
    private ShowCommand show;
    private AddCommand add;
    private RemoveCommand remove;
    private BroadcastCommand broadcast;
    private ManualBroadcastCommand manualBroadcast;

    public BaseCommandHandler(FrogAnnounce plugin, Logger logger){
        this.logger = logger;

        this.reload = new ReloadCommand(plugin);
        this.enable = new EnableCommand(plugin);
        this.disable = new DisableCommand(plugin);
        this.show = new ShowCommand(plugin);
        this.add = new AddCommand(plugin, this.reload);
        this.remove = new RemoveCommand(plugin, this.reload);
        this.broadcast = new BroadcastCommand(plugin);
        this.manualBroadcast = new ManualBroadcastCommand(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //I'll implement real permissions later
        try {
            if (args.length == 0 || StringUtils.anyEqualIgnoreCase(args[0], "help", "h", "?")) {
                sender.sendMessage("/fa reload: Reload the plugin");
                return true;
            }

            //Reload
            if (StringUtils.anyEqualIgnoreCase(args[0], "reload", "r")) {
                checkPermissions(sender, "frogannounce.admin", "frogannounce.*");
                return this.reload.onCommand(sender, command, label, args);
            }

            //Enable
            if (StringUtils.anyEqualIgnoreCase(args[0], "enable", "on")) {
                checkPermissions(sender, "frogannounce.admin", "frogannounce.*");
                return this.enable.onCommand(sender, command, label, args);
            }

            //Disable
            if (StringUtils.anyEqualIgnoreCase(args[0], "disable", "off")) {
                checkPermissions(sender, "frogannounce.admin", "frogannounce.*");
                return this.disable.onCommand(sender, command, label, args);
            }

            //List
            if (StringUtils.anyEqualIgnoreCase(args[0], "list", "l", "show", "s")) {
                checkPermissions(sender, "frogannounce.admin", "frogannounce.*");
                return this.show.onCommand(sender, command, label, args);
            }

            //Add
            if (StringUtils.anyEqualIgnoreCase(args[0], "add", "a")) {
                checkPermissions(sender, "frogannounce.admin", "frogannounce.*");
                return this.add.onCommand(sender, command, label, args);
            }

            //Remove
            if (StringUtils.anyEqualIgnoreCase(args[0], "remove", "rm", "del")) {
                checkPermissions(sender, "frogannounce.admin", "frogannounce.*");
                return this.remove.onCommand(sender, command, label, args);
            }

            //Broadcast
            if (StringUtils.anyEqualIgnoreCase(args[0], "broadcast", "bc")) {
                checkPermissions(sender, "frogannounce.admin", "frogannounce.*");
                return this.broadcast.onCommand(sender, command, label, args);
            }

            //Manual Broadcast
            if (StringUtils.anyEqualIgnoreCase(args[0], "manualbroadcast", "mbc")) {
                checkPermissions(sender, "frogannounce.admin", "frogannounce.*");
                return this.manualBroadcast.onCommand(sender, command, label, args);
            }

            //Ignore
            if (StringUtils.anyEqualIgnoreCase(args[0], "ignore", "optout")) {
                checkPermissions(sender, "frogannounce.optout", "frogannounce.*");
                return false; //NYI
            }

            //Unignore
            if (StringUtils.anyEqualIgnoreCase(args[0], "unignore", "optin")) {
                checkPermissions(sender, "frogannounce.optin", "frogannounce.*");
                return false; //NYI
            }
        }catch(InsufficientPermissionsException e){
            sender.sendMessage(e.getMessage());
            this.logger.log(Level.INFO, sender.getName()+": "+e.getMessage());
            return true;
        }

        return false;
    }

    private void checkPermissions(CommandSender sender, String... nodes) throws InsufficientPermissionsException {
        if(sender instanceof ConsoleCommandSender){
            return;
        }

        for(String node: nodes) {
            if (sender.hasPermission(node)){
                return;
            }
        }

        throw new InsufficientPermissionsException(sender, "Lacking permission; access denied.");
    }
}
