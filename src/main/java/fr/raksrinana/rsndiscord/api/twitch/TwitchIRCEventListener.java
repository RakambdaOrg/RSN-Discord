package fr.raksrinana.rsndiscord.api.twitch;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.extern.log4j.Log4j2;
import net.engio.mbassy.listener.Handler;
import org.jetbrains.annotations.NotNull;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.event.channel.ChannelJoinEvent;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.channel.ChannelNoticeEvent;
import org.kitteh.irc.client.library.event.channel.ChannelPartEvent;
import org.kitteh.irc.client.library.event.client.ClientNegotiationCompleteEvent;
import org.kitteh.irc.client.library.event.connection.ClientConnectionClosedEvent;
import org.kitteh.irc.client.library.feature.twitch.event.ClearChatEvent;
import org.kitteh.irc.client.library.feature.twitch.event.UserNoticeEvent;
import java.util.*;

@Log4j2
public class TwitchIRCEventListener{
	private final Map<String, Collection<GuildTwitchListener>> listeners;
	
	public TwitchIRCEventListener(){
		listeners = new HashMap<>();
	}
	
	public void addListener(@NotNull String channel, @NotNull GuildTwitchListener listener){
		var channelListeners = getListeners(channel);
		channelListeners.add(listener);
		log.info("Loggers listening for channel {}: {}", channel, channelListeners);
	}
	
	public boolean containsListener(@NotNull String channel, long guildId){
		return getListeners(channel).stream()
				.anyMatch(listeners -> Objects.equals(listeners.getGuildId(), guildId));
	}
	
	private Collection<GuildTwitchListener> getListeners(@NotNull Channel channel){
		return getListeners(channel.getName());
	}
	
	public Collection<GuildTwitchListener> getListeners(@NotNull String channel){
		return listeners.computeIfAbsent(channel, key -> new LinkedList<>());
	}
	
	public void removeListener(@NotNull String channel, long guildId){
		getListeners(channel).removeIf(listener -> Objects.equals(listener.getGuildId(), guildId));
	}
	
	public void removeAllListeners(){
		listeners.clear();
	}
	
	@Handler
	private void onClientConnectionEstablishedEvent(ClientNegotiationCompleteEvent event){
		log.info("IRC client negotiations ended, connecting to auto-connect channels");
		Main.getJda().getGuilds()
				.forEach(guild -> Settings.get(guild)
						.getTwitchConfiguration()
						.getTwitchAutoConnectUsers()
						.forEach(user -> {
							try{
								log.info("Auto-connecting to irc user {}", user);
								TwitchUtils.connect(guild, user);
							}
							catch(Exception e){
								log.error("Failed to automatically connect to twitch user {}", user, e);
							}
						}));
	}
	
	@Handler
	public void onClientConnectionCLoseEvent(ClientConnectionClosedEvent event){
		removeAllListeners();
		if(event.canAttemptReconnect()){
			log.warn("IRC connection closed, attempting to reconnect");
			event.setAttemptReconnect(true);
		}
		else{
			log.info("IRC connection closed, cannot reconnect");
		}
	}
	
	@Handler
	public void onChannelJoinEvent(@NotNull ChannelJoinEvent event){
		log.info("Joined IRC channel {}", event.getChannel().getName());
	}
	
	@Handler
	public void onChannelPartEvent(@NotNull ChannelPartEvent event){
		log.info("Left IRC channel {}", event.getChannel().getName());
	}
	
	@Handler
	public void onChannelMessageEvent(@NotNull ChannelMessageEvent event){
		log.info("Received IRC message from {} on channel {} : {}",
				event.getActor().getNick(),
				event.getChannel().getName(),
				event.getMessage());
		
		getListeners(event.getChannel()).forEach(listener -> listener.onChannelMessageEvent(event));
	}
	
	@Handler
	public void onChannelNoticeEvent(@NotNull ChannelNoticeEvent event){
		log.info("Received IRC notice on channel {} : {}",
				event.getChannel().getName(),
				event.getMessage());
		
		getListeners(event.getChannel()).forEach(listener -> listener.onChannelNoticeEvent(event));
	}
	
	@Handler
	public void onClearChatEvent(@NotNull ClearChatEvent event){
		log.info("IRC chat cleared {}", event.getChannel().getName());
		
		getListeners(event.getChannel()).forEach(listener -> listener.onClearChatEvent(event));
	}
	
	@Handler
	public void onUserNoticeEvent(@NotNull UserNoticeEvent event){
		log.info("Received IRC user notice : {}", event.getMessage().orElse(""));
		
		getListeners(event.getChannel()).forEach(listener -> listener.onUserNoticeEvent(event));
	}
}
