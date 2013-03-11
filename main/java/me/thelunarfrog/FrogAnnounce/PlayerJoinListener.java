package main.java.me.thelunarfrog.FrogAnnounce;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener{
	private final FrogAnnounce plugin;

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent evt){
		if(this.plugin.showJoinMessage)
			if(!this.plugin.ignoredPlayers.contains(evt.getPlayer().getName()))
				evt.getPlayer().sendMessage(this.plugin.colourizeText(this.plugin.joinMessage));
	}

	protected PlayerJoinListener(final FrogAnnounce plugin){
		this.plugin = plugin;
	}
}