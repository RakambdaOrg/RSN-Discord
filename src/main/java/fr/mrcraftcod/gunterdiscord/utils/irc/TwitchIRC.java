package fr.mrcraftcod.gunterdiscord.utils.irc;

import net.dv8tion.jda.api.entities.Guild;
import org.kitteh.irc.client.library.Client;
import java.util.Objects;

public class TwitchIRC{
	private static final String NICKNAME = "RakSrinaNa";
	private static Client CLIENT = null;
	
	public static void connect(Guild guild, String user){
		if(Objects.isNull(CLIENT)){
			CLIENT = Client.builder().nick(NICKNAME).server().host("irc.chat.twitch.tv").port(6667).secureKeyPassword(String.format("oauth:%s", System.getenv("TWITCH_TOKEN"))).then().buildAndConnect();
		}
		String channel = String.format("#%s", user);
		if(CLIENT.getChannels().stream().noneMatch(channel1 -> Objects.equals(channel1.getName(), channel))){
			CLIENT.addChannel(channel);
			CLIENT.getEventManager().registerEventListener(new TwitchIRCListener(guild, user));
		}
	}
	
	public static void disconnect(String user){
		if(Objects.nonNull(CLIENT)){
			String channel = String.format("#%s", user);
			CLIENT.removeChannel(channel);
			CLIENT.getEventManager().getRegisteredEventListeners().removeIf(obj -> obj instanceof TwitchIRCListener && Objects.equals(((TwitchIRCListener) obj).getUser(), user));
			if(CLIENT.getChannels().isEmpty()){
				CLIENT.shutdown();
				CLIENT = null;
			}
		}
	}
}
