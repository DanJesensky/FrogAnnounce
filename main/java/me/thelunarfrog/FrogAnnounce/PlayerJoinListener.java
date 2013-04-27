package main.java.me.thelunarfrog.FrogAnnounce;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener{
	private final FrogAnnounce plugin;

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent evt){
		if(!this.plugin.ignoredPlayers.contains(evt.getPlayer().getName()))
			for(final String s: this.plugin.joinMessage.split("&NEW_LINE;"))
				if(this.plugin.tag==""||this.plugin.tag==null)
					evt.getPlayer().sendMessage(this.plugin.colourizeText(s));
				else
					evt.getPlayer().sendMessage(this.plugin.tag+" "+this.plugin.colourizeText(s));
	}

	protected PlayerJoinListener(final FrogAnnounce plugin){
		this.plugin = plugin;
	}
}