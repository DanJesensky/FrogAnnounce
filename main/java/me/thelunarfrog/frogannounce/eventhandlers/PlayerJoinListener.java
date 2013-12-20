package me.thelunarfrog.frogannounce.eventhandlers;

import me.thelunarfrog.frogannounce.FrogAnnounce;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener{
	private final FrogAnnounce plugin;

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent evt){
		if(!this.plugin.getIgnoredPlayers().contains(evt.getPlayer().getName())){
			for(final String s : this.plugin.getJoinMessage().split("&NEW_LINE;"))
				if(this.plugin.getTag().isEmpty() || (this.plugin.getTag() == null)){
					evt.getPlayer().sendMessage(FrogAnnounce.colourizeText(s));
				}else
					evt.getPlayer().sendMessage(this.plugin.getTag() + " " + FrogAnnounce.colourizeText(s));
		}
	}

	public PlayerJoinListener(final FrogAnnounce plugin){
		this.plugin = plugin;
	}
}