package com.danjesensky.frogannounce;

import com.danjesensky.frogannounce.infrastructure.announcements.Announcement;
import com.danjesensky.frogannounce.infrastructure.announcements.IndependentAnnouncement;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Random;

public class Announcer implements Runnable {
    private List<Announcement> announcements;
    private int index;
    private boolean isRandom;
    private Random rand;
    private JavaPlugin plugin;

    public Announcer(List<Announcement> announcements, JavaPlugin plugin, boolean isRandom){
        this.announcements = announcements;
        this.plugin = plugin;
        this.index = 0;
        this.isRandom = isRandom;

        if(isRandom){
            this.rand = new Random();
        }
    }

    public List<Announcement> getAnnouncements(){
        return this.announcements;
    }

    @Override
    public void run() {
        if(announcements.size() == 0){
            return;
        }
        Announcement a;

        if(this.isRandom){
            while((a = this.announcements.get(this.rand.nextInt(this.announcements.size())))
                    instanceof IndependentAnnouncement);
        }else {
            do{
                a = this.announcements.get(this.index++);
                if(this.index >= this.announcements.size()){
                    this.index = 0;
                }
            }while(a instanceof IndependentAnnouncement);
        }

        a.invoke();
    }
}
