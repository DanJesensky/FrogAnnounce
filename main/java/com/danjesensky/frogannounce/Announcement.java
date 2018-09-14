package com.danjesensky.frogannounce;

import org.bukkit.Bukkit;

public abstract class Announcement {
    private String text;
    private String key;

    public Announcement(String key, String text){
        this.key = key;
        this.text = text;
    }

    public String getText(){
        return this.text;
    }

    public String getKey(){
        return this.key;
    }

    public void invoke(){
        Bukkit.getServer().broadcastMessage(this.text);
    }

    @Override
    public String toString(){
        return "Announcement["+this.text+"]";
    }
}
