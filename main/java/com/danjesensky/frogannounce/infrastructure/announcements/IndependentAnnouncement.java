package com.danjesensky.frogannounce.infrastructure.announcements;

import com.danjesensky.frogannounce.infrastructure.announcements.Announcement;

public class IndependentAnnouncement extends Announcement {
    private int interval;

    public IndependentAnnouncement(String key, String text, int interval){
        super(key, text);
        this.interval = interval * 60000;
    }
}
