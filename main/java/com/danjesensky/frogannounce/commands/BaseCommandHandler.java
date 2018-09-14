package com.danjesensky.frogannounce.commands;

import com.danjesensky.frogannounce.FrogAnnounce;
import com.danjesensky.frogannounce.exceptions.InsufficientPermissionsException;
import com.danjesensky.frogannounce.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.util.StringUtil;

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

    public BaseCommandHandler(FrogAnnounce plugin, Logger logger){
        this.logger = logger;
        this.reload = new ReloadCommand(plugin);
        this.enable = new EnableCommand(plugin);
        this.disable = new DisableCommand(plugin);
        this.show = new ShowCommand(plugin);
        this.add = new AddCommand(plugin, this.reload);
        this.remove = new RemoveCommand(plugin, this.reload);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //I'll implement real permissions later
        try {
            checkPermissions(sender, "frogannounce.*");
        }catch(InsufficientPermissionsException e){
            sender.sendMessage(e.getMessage());
            this.logger.log(Level.INFO, sender.getName()+": "+e.getMessage());
            return true;
        }

        if(args.length == 0 || StringUtils.anyEqualIgnoreCase(args[0], "help", "h", "?")){
            sender.sendMessage("/fa reload: Reload the plugin");
            return true;
        }

        //Reload
        if(StringUtils.anyEqualIgnoreCase(args[0], "reload", "r")){
            return this.reload.onCommand(sender, command, label, args);
        }

        //Enable
        if(StringUtils.anyEqualIgnoreCase(args[0], "enable", "on")){
            return this.enable.onCommand(sender, command, label, args);
        }

        //Disable
        if(StringUtils.anyEqualIgnoreCase(args[0], "disable", "off")){
            return this.disable.onCommand(sender, command, label, args);
        }

        //List
        if(StringUtils.anyEqualIgnoreCase(args[0], "list", "l", "show", "s")){
            return this.show.onCommand(sender, command, label, args);
        }

        //Add
        if(StringUtils.anyEqualIgnoreCase(args[0], "add", "a")){
            return this.add.onCommand(sender, command, label, args);
        }

        //Remove
        if(StringUtils.anyEqualIgnoreCase(args[0], "remove", "rm", "del")){
            return this.remove.onCommand(sender, command, label, args);
        }

        //Ignore
        if(StringUtils.anyEqualIgnoreCase(args[0], "ignore", "optout")){
            return false; //NYI
        }

        //Unignore
        if(StringUtils.anyEqualIgnoreCase(args[0], "unignore", "optin")){
            return false; //NYI
        }

        return false;
    }

    private void checkPermissions(CommandSender sender, String node) throws InsufficientPermissionsException {
        if(!sender.hasPermission("") && !(sender instanceof ConsoleCommandSender)){
            throw new InsufficientPermissionsException(sender, "Lacking permission \""+node+"\"; access denied.");
        }
    }
}
