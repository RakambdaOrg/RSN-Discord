package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import fr.mrcraftcod.gunterdiscord.utils.irc.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
class IRCUtils{
	private static final Pattern EVENT_PATTERN = Pattern.compile("^(@([^\\s]+)\\s)?:([^\\s]+)\\s([^\\s]+)\\s([^:]+)\\s?(:(.*))?$");
	private static final Logger LOGGER = LoggerFactory.getLogger(IRCUtils.class);
	
	@Nonnull
	static Optional<IRCMessage> buildEvent(@Nonnull final String message){
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
						return Optional.of(new UserNoticeIRCMessage(tags, matcher.group(7)));
					case "CLEARCHAT":
						return Optional.of(new ClearChatIRCMessage(tags, matcher.group(7)));
					case "CLEARMSG":
						return Optional.of(new ClearMessageIRCMessage(tags, matcher.group(7)));
				}
			}
			Optional.ofNullable(Main.getJDA().getUserById(Utilities.RAKSRINANA_ACCOUNT)).ifPresent(rsn -> rsn.openPrivateChannel().queue(c -> c.sendMessage("Unknown IRC message: " + message).queue()));
			LOGGER.warn("Unknown IRC message: {}", message);
			return Optional.empty();
		}
		catch(final Exception e){
			LOGGER.error("Failed to handle IRC message: {}", message);
		}
		return Optional.empty();
	}
	
	private static List<IRCTag> getTags(final String tags){
		return Optional.ofNullable(tags).stream().flatMap(t -> Arrays.stream(t.split(";"))).map(t -> {
			final var eq = t.indexOf('=');
			if(eq >= 0){
				return new IRCTag(t.substring(0, eq), t.substring(eq + 1));
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}
}
