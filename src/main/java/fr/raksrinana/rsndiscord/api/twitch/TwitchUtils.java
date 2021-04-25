package fr.raksrinana.rsndiscord.api.twitch;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.element.Channel;
import org.kitteh.irc.client.library.feature.twitch.TwitchSupport;
import java.util.Objects;
import java.util.Optional;
import static org.kitteh.irc.client.library.Client.Builder.Server.SecurityType.SECURE;

public class TwitchUtils{
	private static final String TWITCH_IRC_HOST = "irc.chat.twitch.tv";
	private static TwitchIRCEventListener listener;
	private static Client ircClient;
	
	public static void connect(Guild guild, String channel){
		var client = getIrcClient();
		var ircChannelName = "#%s".formatted(channel);
		var channelId = Settings.get(guild).getTwitchConfiguration()
				.getTwitchChannel()
				.map(ChannelConfiguration::getChannelId)
				.orElseThrow(() -> new RuntimeException("Twitch connection isn't enabled"));
		
		client.addChannel(ircChannelName);
		var ircChannel = tryGettingChannel(ircChannelName, 40);
		
		Log.getLogger(guild).info("Added Twitch listener for channel {} and guild {}", ircChannel, guild);
		listener.addListener(ircChannel, new GuildTwitchListener(guild.getIdLong(), channelId));
	}
	
	private static Channel tryGettingChannel(String name, int attemptsLeft){
		var channel = getIrcClient().getChannel(name);
		if(channel.isPresent()){
			return channel.get();
		}
		
		if(attemptsLeft <= 0){
			throw new RuntimeException("Couldn't get channel info");
		}
		
		try{
			Thread.sleep(250);
		}
		catch(InterruptedException e){
			Log.getLogger().warn("Error while sleeping waiting for IRC channel", e);
		}
		
		return tryGettingChannel(name, attemptsLeft - 1);
	}
	
	public static void disconnect(Guild guild, String channel){
		var client = getIrcClient();
		var ircChannelName = "#%s".formatted(channel);
		
		var ircChannel = client.getChannel(ircChannelName)
				.orElseThrow(() -> new RuntimeException("Couldn't get channel info"));
		client.removeChannel(ircChannelName);
		
		Log.getLogger(guild).info("Removed Twitch listener for channel {}", ircChannelName);
		listener.removeListener(ircChannel, guild.getIdLong());
	}
	
	public static void disconnectAll(){
		var client = getIrcClient();
		client.getChannels().forEach(channel -> client.removeChannel(channel.getName()));
		
		Log.getLogger().info("Removed all Twitch listeners for channel");
		listener.removeAllListeners();
	}
	
	public static void close(){
		Optional.ofNullable(ircClient).ifPresent(Client::shutdown);
	}
	
	public static void connect(){
		getIrcClient();
	}
	
	private static Client getIrcClient(){
		if(Objects.isNull(ircClient)){
			Log.getLogger().info("Creating new Twitch IRC client");
			
			ircClient = Client.builder()
					.server()
					.host(TWITCH_IRC_HOST).port(443, SECURE)
					.password(getPassword()).then()
					.nick(getUsername())
					.build();
			
			TwitchSupport.addSupport(ircClient);
			ircClient.connect();
			
			listener = new TwitchIRCEventListener();
			ircClient.getEventManager().registerEventListener(listener);
			Log.getLogger().info("IRC Client created");
		}
		
		return ircClient;
	}
	
	private static String getPassword(){
		return "oauth:" + System.getProperty("TWITCH_TOKEN");
	}
	
	private static String getUsername(){
		return System.getProperty("TWITCH_NICKNAME");
	}
}
