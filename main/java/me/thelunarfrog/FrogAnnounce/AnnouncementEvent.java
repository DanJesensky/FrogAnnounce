package main.java.me.thelunarfrog.FrogAnnounce;

public class AnnouncementEvent{
	private final String announcement;
	private final boolean isAutomatic;
	private final int index;

	public AnnouncementEvent(String announcementString, boolean isAutomatic, int index){
		this.announcement = announcementString;
		this.isAutomatic = isAutomatic;
		this.index = index;
	}

	public boolean isAutomatic(){
		return this.isAutomatic;
	}

	public String getAnnouncement(){
		return this.announcement;
	}

	public int getAnnouncementIndex(){
		return this.index;
	}
}