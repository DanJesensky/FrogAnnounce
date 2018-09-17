package com.danjesensky.frogannounce.infrastructure.announcements;

import com.danjesensky.frogannounce.infrastructure.announcements.Announcement;

public class QueuedAnnouncement extends Announcement {
    public QueuedAnnouncement(String key, String text){
        super(key, text);
    }
}
