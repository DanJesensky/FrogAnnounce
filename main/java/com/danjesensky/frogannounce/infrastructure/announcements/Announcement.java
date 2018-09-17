package com.danjesensky.frogannounce.infrastructure.announcements;

import com.danjesensky.frogannounce.utils.StringUtils;
import org.bukkit.Bukkit;

public abstract class Announcement implements Runnable {
    private String text;
    private String key;

    public Announcement(String key, String text){
        this.key = key;
        this.text = StringUtils.recolorText(text);
    }

    public String getText(){
        return this.text;
    }

    public String getKey(){
        return this.key;
    }

    @Override
    public void run(){
        Bukkit.getServer().broadcastMessage(this.text);
    }

    @Override
    public String toString(){
        return "Announcement["+this.text+"]";
    }
}
