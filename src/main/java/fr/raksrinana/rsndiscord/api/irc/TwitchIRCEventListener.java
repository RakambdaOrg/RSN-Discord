package fr.raksrinana.rsndiscord.api.irc;

import fr.raksrinana.rsndiscord.log.Log;
import org.jetbrains.annotations.NotNull;
import net.engio.mbassy.listener.Handler;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.event.channel.ChannelJoinEvent;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.channel.ChannelNoticeEvent;
import org.kitteh.irc.client.library.event.channel.ChannelPartEvent;
import org.kitteh.irc.client.library.feature.twitch.event.ClearChatEvent;
import org.kitteh.irc.client.library.feature.twitch.event.UserNoticeEvent;
import java.util.*;

public class TwitchIRCEventListener{
	private final Map<Channel, Collection<GuildTwitchListener>> listeners;
	
	public TwitchIRCEventListener(){
		this.listeners = new HashMap<>();
	}
	
	public void addListener(@NotNull Channel channel, @NotNull GuildTwitchListener listener){
		getListeners(channel).add(listener);
	}
	
	public Collection<GuildTwitchListener> getListeners(@NotNull Channel channel){
		return listeners.computeIfAbsent(channel, key -> new LinkedList<>());
	}
	
	public void removeListener(@NotNull Channel channel, long guildId){
		getListeners(channel).removeIf(listener -> Objects.equals(listener.getGuildId(), guildId));
	}
	
	public void removeAllListeners(){
		listeners.clear();
	}
	
	@Handler
	public void onChannelJoinEvent(@NotNull ChannelJoinEvent event){
		Log.getLogger().info("Joined IRC channel {}", event.getChannel().getName());
	}
	
	@Handler
	public void onChannelPartEvent(@NotNull ChannelPartEvent event){
		Log.getLogger().info("Left IRC channel {}", event.getChannel().getName());
	}
	
	@Handler
	public void onChannelMessageEvent(@NotNull ChannelMessageEvent event){
		Log.getLogger().info("Received IRC message from {} on channel {} : {}",
				event.getActor().getNick(),
				event.getChannel().getName(),
				event.getMessage());
		
		getListeners(event.getChannel()).forEach(listener -> listener.onChannelMessageEvent(event));
	}
	
	@Handler
	public void onChannelNoticeEvent(@NotNull ChannelNoticeEvent event){
		Log.getLogger().info("Received IRC notice on channel {} : {}",
				event.getChannel().getName(),
				event.getMessage());
		
		getListeners(event.getChannel()).forEach(listener -> listener.onChannelNoticeEvent(event));
	}
	
	@Handler
	public void onClearChatEvent(@NotNull ClearChatEvent event){
		Log.getLogger().info("IRC chat cleared {}", event.getChannel().getName());
		
		getListeners(event.getChannel()).forEach(listener -> listener.onClearChatEvent(event));
	}
	
	@Handler
	public void onUserNoticeEvent(@NotNull UserNoticeEvent event){
		Log.getLogger().info("Received IRC user notice : {}", event.getMessage().orElse(""));
		
		getListeners(event.getChannel()).forEach(listener -> listener.onUserNoticeEvent(event));
	}
}
