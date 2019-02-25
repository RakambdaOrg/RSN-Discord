package fr.mrcraftcod.gunterdiscord.utils.irc;

import net.dv8tion.jda.api.entities.Guild;
import java.io.IOException;
import java.util.Objects;

public class TwitchIRC{
	private static final String NICKNAME = "RakSrinaNa";
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
	
	public static void disconnect(Guild guild, String user) throws IOException{
		if(Objects.nonNull(CLIENT)){
			String channel = String.format("#%s", user);
			CLIENT.getListeners().removeIf(obj -> obj instanceof TwitchIRCListener && Objects.equals(((TwitchIRCListener) obj).getUser(), user) && Objects.equals(((TwitchIRCListener) obj).getGuild(), guild));
			if(CLIENT.getListeners().stream().noneMatch(l -> l.handlesChannel(channel))){
				CLIENT.leaveChannel(channel);
			}
			if(CLIENT.getJoinedChannels().isEmpty()){
				CLIENT.close();
				CLIENT = null;
			}
		}
	}
}
