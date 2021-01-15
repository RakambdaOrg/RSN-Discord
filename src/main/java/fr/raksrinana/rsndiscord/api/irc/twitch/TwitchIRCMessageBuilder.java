package fr.raksrinana.rsndiscord.api.irc.twitch;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.api.irc.IIRCMessageBuilder;
import fr.raksrinana.rsndiscord.api.irc.messages.*;
import fr.raksrinana.rsndiscord.api.irc.twitch.messages.*;
import fr.raksrinana.rsndiscord.log.Log;
import lombok.NonNull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import static fr.raksrinana.rsndiscord.utils.Utilities.MAIN_RAKSRINANA_ACCOUNT;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

class TwitchIRCMessageBuilder implements IIRCMessageBuilder{
	private static final Pattern EVENT_PATTERN = Pattern.compile("^(@([^\\s]+)\\s)?:([^\\s]+)\\s([^\\s]+)\\s([^:]+)\\s?(:(.*))?$");
	
	@NonNull
	public Optional<IIRCMessage> buildEvent(@NonNull final String message){
		try{
			if("PING :tmi.twitch.tv".equals(message)){
				return Optional.of(new PingIRCMessage());
			}
			final var matcher = EVENT_PATTERN.matcher(message);
			if(matcher.matches()){
				final var tags = getTags(matcher.group(2));
				final var user = new IRCUser(matcher.group(3));
				switch(matcher.group(4)){
					case "001":
					case "002":
					case "003":
					case "004":
					case "353":
					case "366":
					case "372":
					case "375":
					case "376":
					case "CAP":
						return Optional.of(new InfoMessageIRCMessage(matcher.group(7)));
					case "JOIN":
						return Optional.of(new ChannelJoinIRCMessage(user, matcher.group(5).trim()));
					case "PART":
						return Optional.of(new ChannelLeftIRCMessage(user, matcher.group(5).trim()));
					case "PRIVMSG":
						return Optional.of(new ChannelMessageIRCMessage(tags, user, matcher.group(5).trim(), matcher.group(7)));
					case "USERSTATE":
						return Optional.of(new UserStateIRCMessage(tags, matcher.group(5)));
					case "ROOMSTATE":
						return Optional.of(new RoomStateIRCMessage(tags, matcher.group(5)));
					case "USERNOTICE":
						return Optional.of(new UserNoticeIRCMessage(tags, matcher.group(5).trim(), matcher.group(7)));
					case "CLEARCHAT":
						return Optional.of(new ClearChatIRCMessage(tags, matcher.group(5).trim(), matcher.group(7)));
					case "CLEARMSG":
						return Optional.of(new ClearMessageIRCMessage(tags, matcher.group(5).trim(), matcher.group(7)));
					case "NOTICE":
						return Optional.of(new NoticeIRCMessage(tags, matcher.group(5).trim(), matcher.group(7)));
					case "HOSTTARGET":
						return Optional.of(new HostTargetIRCMessage(matcher.group(5).trim(), matcher.group(7)));
				}
			}
			ofNullable(Main.getJda().getUserById(MAIN_RAKSRINANA_ACCOUNT))
					.ifPresent(user -> user.openPrivateChannel().submit()
							.thenAccept(privateChannel -> privateChannel.sendMessage("Unknown IRC message: " + message).submit()));
			Log.getLogger(null).warn("Unknown IRC message: {}", message);
			return Optional.empty();
		}
		catch(final Exception e){
			Log.getLogger(null).error("Failed to handle IRC message: {}", message);
		}
		return Optional.empty();
	}
	
	private static List<IRCTag> getTags(final String tags){
		return ofNullable(tags).stream()
				.flatMap(t -> Arrays.stream(t.split(";")))
				.map(t -> {
					final var eq = t.indexOf('=');
					if(eq >= 0){
						return new IRCTag(t.substring(0, eq), t.substring(eq + 1));
					}
					return null;
				})
				.filter(Objects::nonNull)
				.collect(toList());
	}
}
