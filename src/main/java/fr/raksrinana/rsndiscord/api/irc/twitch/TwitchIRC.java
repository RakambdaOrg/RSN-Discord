package fr.raksrinana.rsndiscord.api.irc.twitch;

import fr.raksrinana.rsndiscord.api.irc.IRCClient;
import fr.raksrinana.rsndiscord.api.irc.messages.ChannelLeftIRCMessage;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.io.IOException;
import java.util.Objects;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class TwitchIRC{
	private static IRCClient client = null;
	
	public static void connect(@NonNull final Guild guild, @NonNull final String user) throws IOException{
		if(isNull(client)){
			client = new IRCClient("irc.chat.twitch.tv", 6667, new TwitchIRCMessageBuilder());
			client.setSecureKeyPassword(String.format("oauth:%s", System.getProperty("TWITCH_TOKEN")));
			client.connect();
			client.setNick(System.getProperty("TWITCH_NICKNAME"));
			client.sendMessage("CAP REQ :twitch.tv/tags");
			client.sendMessage("CAP REQ :twitch.tv/commands");
		}
		var channel = String.format("#%s", user.toLowerCase());
		if(client.getListeners().stream().noneMatch(l -> Objects.equals(l.getIrcChannel(), channel) && Objects.equals(guild, l.getGuild()))){
			var listener = new TwitchIRCListener(guild, user, channel);
			if(Settings.get(guild).getTwitchConfiguration().isIrcForward()){
				guild.getJDA().addEventListener(listener);
			}
			client.addEventListener(listener);
			client.joinChannel(channel);
		}
	}
	
	public static void disconnect(@NonNull final Guild guild, @NonNull final String user){
		disconnect(guild, user, true);
	}
	
	public static void disconnect(@NonNull final Guild guild, @NonNull final String user, final boolean removeListener){
		if(isNull(client)){
			return;
		}
		
		var ircChannel = String.format("#%s", user.toLowerCase());
		client.getListeners().stream()
				.filter(l -> Objects.equals(l.getUser(), user) && Objects.equals(guild, l.getGuild()))
				.forEach(l -> l.onIRCMessage(new ChannelLeftIRCMessage(new IRCUser(""), ircChannel)));
		if(removeListener){
			client.getListeners().removeIf(obj -> {
				if(obj instanceof TwitchIRCListener && Objects.equals(obj.getUser(), user) && Objects.equals(guild, obj.getGuild())){
					guild.getJDA().removeEventListener(obj);
					return true;
				}
				return false;
			});
		}
		if(client.getListeners().stream().noneMatch(l -> Objects.equals(l.getUser(), user))){
			client.leaveChannel(ircChannel);
		}
		if(client.getJoinedChannels().isEmpty()){
			close();
		}
	}
	
	public static void close(){
		if(nonNull(client)){
			try{
				client.close();
			}
			catch(final IOException e){
				Log.getLogger(null).error("Error closing IRC connection", e);
			}
			client = null;
		}
	}
	
	public static void sendMessage(final Guild guild, @NonNull final String ircChannel, @NonNull final String message){
		if(nonNull(client) && client.getJoinedChannels().contains(ircChannel)){
			Log.getLogger(guild).info("Sending IRC message tp {}: {}", ircChannel, message);
			client.sendMessage(String.format("PRIVMSG %s :%s", ircChannel, message));
		}
	}
}
