package com.danjesensky.frogannounce.exceptions;

import org.bukkit.command.CommandSender;

public class InsufficientPermissionsException extends Exception {
    private CommandSender sender;

    public InsufficientPermissionsException(){
        super("You don't have permission to do that.");
    }

    public InsufficientPermissionsException(CommandSender sender, String s) {
        super(s);
    }

    public CommandSender getSender() {
        return sender;
    }
}
