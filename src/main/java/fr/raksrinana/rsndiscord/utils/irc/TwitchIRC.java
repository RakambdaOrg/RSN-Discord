package fr.raksrinana.rsndiscord.utils.irc;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.irc.messages.ChannelLeftIRCMessage;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TwitchIRC{
	private static IRCClient CLIENT = null;
	
	public static void connect(@NonNull final Guild guild, @NonNull final String user) throws IOException{
		if(Objects.isNull(CLIENT)){
			CLIENT = new IRCClient("irc.chat.twitch.tv", 6667);
			CLIENT.setSecureKeyPassword(String.format("oauth:%s", System.getProperty("TWITCH_TOKEN")));
			CLIENT.connect();
			CLIENT.setNick(System.getProperty("TWITCH_NICKNAME"));
			CLIENT.sendMessage("CAP REQ :twitch.tv/tags");
			CLIENT.sendMessage("CAP REQ :twitch.tv/commands");
		}
		final var channel = String.format("#%s", user.toLowerCase());
		if(CLIENT.getListeners().stream().noneMatch(l -> Objects.equals(l.getIrcChannel(), channel) && Objects.equals(guild, l.getGuild()))){
			final var listener = new TwitchIRCListener(guild, user, channel);
			if(Settings.get(guild).getIrcForward()){
				guild.getJDA().addEventListener(listener);
			}
			CLIENT.addEventListener(listener);
			CLIENT.joinChannel(channel);
		}
	}
	
	public static void disconnect(@NonNull final Guild guild, @NonNull final String user){
		disconnect(guild, user, true);
	}
	
	static void disconnect(@NonNull final Guild guild, @NonNull final String user, final boolean removeListener){
		if(Objects.nonNull(CLIENT)){
			final var ircChannel = String.format("#%s", user.toLowerCase());
			CLIENT.getListeners().stream().filter(l -> Objects.equals(l.getUser(), user) && Objects.equals(guild, l.getGuild())).forEach(l -> l.onIRCMessage(new ChannelLeftIRCMessage(new IRCUser(""), ircChannel)));
			if(removeListener){
				CLIENT.getListeners().removeIf(obj -> {
					if(obj instanceof TwitchIRCListener && Objects.equals(obj.getUser(), user) && Objects.equals(guild, obj.getGuild())){
						guild.getJDA().removeEventListener(obj);
						return true;
					}
					return false;
				});
			}
			if(CLIENT.getListeners().stream().noneMatch(l -> Objects.equals(l.getUser(), user))){
				CLIENT.leaveChannel(ircChannel);
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
				Log.getLogger(null).error("Error closing IRC connection", e);
			}
			CLIENT = null;
		}
	}
	
	public static void sendMessage(final Guild guild, @NonNull final String ircChannel, @NonNull final String message){
		if(Objects.nonNull(CLIENT) && CLIENT.getJoinedChannels().contains(ircChannel)){
			Log.getLogger(guild).info("Sending IRC message tp {}: {}", ircChannel, message);
			CLIENT.sendMessage(String.format("PRIVMSG %s :%s", ircChannel, message));
		}
	}
	
	@NonNull
	public static Set<String> getConnectedTo(){
		if(Objects.nonNull(CLIENT)){
			return CLIENT.getListeners().stream().map(IRCListener::getUser).distinct().collect(Collectors.toSet());
		}
		return Set.of();
	}
}
