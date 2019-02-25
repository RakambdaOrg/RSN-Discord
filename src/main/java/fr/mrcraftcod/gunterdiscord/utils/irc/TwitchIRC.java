package fr.mrcraftcod.gunterdiscord.utils.irc;

import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Objects;

public class TwitchIRC{
	private static final Logger LOGGER = LoggerFactory.getLogger(TwitchIRC.class);
	private static final String NICKNAME = "raksrinana";
	private static IRCClient CLIENT = null;
	
	public static void connect(Guild guild, String user) throws IOException{
		if(Objects.isNull(CLIENT)){
			CLIENT = new IRCClient("irc.chat.twitch.tv", 6667);
			CLIENT.setSecureKeyPassword(String.format("oauth:%s", System.getenv("TWITCH_TOKEN")));
			CLIENT.connect();
			CLIENT.setNick(NICKNAME);
		}
		String channel = String.format("#%s", user);
		if(CLIENT.getJoinedChannels().stream().noneMatch(joinedChannel -> Objects.equals(joinedChannel, channel))){
			final var listener = new TwitchIRCListener(guild, user, channel);
			CLIENT.addEventListener(listener);
			CLIENT.joinChannel(channel);
		}
	}
	
	public static void disconnect(Guild guild, String user){
		if(Objects.nonNull(CLIENT)){
			String channel = String.format("#%s", user);
			CLIENT.leaveChannel(channel);
			CLIENT.getListeners().removeIf(obj -> obj instanceof TwitchIRCListener && Objects.equals(((TwitchIRCListener) obj).getUser(), user) && Objects.equals(((TwitchIRCListener) obj).getGuild(), guild));
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
			catch(IOException e){
				LOGGER.error("Error closing IRC connection", e);
			}
			CLIENT = null;
		}
	}
}
