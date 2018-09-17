package com.danjesensky.frogannounce.infrastructure.announcements;

import com.danjesensky.frogannounce.infrastructure.announcements.Announcement;

public class MisconfiguredAnnouncement extends Announcement {
    public MisconfiguredAnnouncement(){
        super("0", "FrogAnnounce appears to be misconfigured.");
    }
}
