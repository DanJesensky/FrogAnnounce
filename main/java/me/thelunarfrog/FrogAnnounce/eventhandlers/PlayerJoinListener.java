package main.java.me.thelunarfrog.FrogAnnounce.eventhandlers;

import main.java.me.thelunarfrog.FrogAnnounce.FrogAnnounce;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener{
	private final FrogAnnounce plugin;

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent evt){
		if(!this.plugin.getIgnoredPlayers().contains(evt.getPlayer().getName()))
			for(final String s: this.plugin.getJoinMessage().split("&NEW_LINE;"))
				if(this.plugin.getTag()==""||this.plugin.getTag()==null)
					evt.getPlayer().sendMessage(this.plugin.colourizeText(s));
				else
					evt.getPlayer().sendMessage(this.plugin.getTag()+" "+this.plugin.colourizeText(s));
	}

	public PlayerJoinListener(final FrogAnnounce plugin){
		this.plugin = plugin;
	}
}