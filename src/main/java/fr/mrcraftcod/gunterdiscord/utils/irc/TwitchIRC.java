package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TwitchIRC{
	private static final Logger LOGGER = LoggerFactory.getLogger(TwitchIRC.class);
	private static final String NICKNAME = "raksrinana";
	private static IRCClient CLIENT = null;
	
	public static void connect(@Nonnull final Guild guild, @Nonnull final String user) throws IOException{
		if(Objects.isNull(CLIENT)){
			CLIENT = new IRCClient("irc.chat.twitch.tv", 6667);
			CLIENT.setSecureKeyPassword(String.format("oauth:%s", System.getProperty("TWITCH_TOKEN")));
			CLIENT.connect();
			CLIENT.setNick(NICKNAME);
		}
		final var channel = String.format("#%s", user.toLowerCase());
		if(CLIENT.getJoinedChannels().stream().noneMatch(joinedChannel -> Objects.equals(joinedChannel, channel))){
			final var listener = new TwitchIRCListener(guild, user, channel);
			if(NewSettings.getConfiguration(guild).getIrcForward()){
				guild.getJDA().addEventListener(listener);
			}
			CLIENT.addEventListener(listener);
			CLIENT.joinChannel(channel);
		}
	}
	
	public static void disconnect(@Nonnull final Guild guild, @Nonnull final String user){
		disconnect(guild, user, true);
	}
	
	static void disconnect(@Nonnull final Guild guild, @Nonnull final String user, final boolean removeListener){
		if(Objects.nonNull(CLIENT)){
			final var channel = String.format("#%s", user.toLowerCase());
			CLIENT.leaveChannel(channel);
			if(removeListener){
				CLIENT.getListeners().removeIf(obj -> {
					if(obj instanceof TwitchIRCListener && Objects.equals(obj.getUser(), user) && Objects.equals(obj.getGuild(), guild)){
						guild.getJDA().removeEventListener(obj);
						return true;
					}
					return false;
				});
			}
			if(CLIENT.getJoinedChannels().isEmpty()){
				close();
			}
		}
	}
	
	public static void close(){
		if(Objects.nonNull(CLIENT)){
			try{
				CLIENT.close();
			}
			catch(final IOException e){
				LOGGER.error("Error closing IRC connection", e);
			}
			CLIENT = null;
		}
	}
	
	public static void sendMessage(@Nullable Guild guild, @Nonnull String ircChannel, @Nonnull String message){
		if(CLIENT.getJoinedChannels().contains(ircChannel)){
			Log.getLogger(guild).info("Sending IRC message tp {}: {}", ircChannel, message);
			CLIENT.sendMessage(String.format("PRIVMSG %s :%s", ircChannel, message));
		}
	}
	
	@Nonnull
	public static List<String> getConnectedTo(){
		if(Objects.nonNull(CLIENT)){
			return CLIENT.getListeners().stream().map(IRCListener::getUser).distinct().collect(Collectors.toList());
		}
		return List.of();
	}
}
