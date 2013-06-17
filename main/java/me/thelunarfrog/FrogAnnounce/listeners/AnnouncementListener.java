package main.java.me.thelunarfrog.FrogAnnounce.listeners;

import main.java.me.thelunarfrog.FrogAnnounce.events.AnnouncementEvent;

public interface AnnouncementListener{
	// @ListenerType(listenerType = AnnotType.ASYNC)
	public void onAnnounceEvent(AnnouncementEvent evt);
}