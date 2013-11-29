package me.thelunarfrog.frogannounce.events;

import me.thelunarfrog.frogannounce.Announcement;

public class AnnouncementEvent{
	private final Announcement announcement;
	private final boolean isAutomatic;
	private final int index;

	public Announcement getAnnouncement(){
		return this.announcement;
	}

	public int getAnnouncementIndex(){
		return this.index;
	}

	public boolean isAutomatic(){
		return this.isAutomatic;
	}

	public AnnouncementEvent(final Announcement a, final boolean isAutomatic, final int index){
		this.announcement = a;
		this.isAutomatic = isAutomatic;
		this.index = index;
	}
}