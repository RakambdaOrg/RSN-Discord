package fr.mrcraftcod.gunterdiscord.utils.irc;

import fr.mrcraftcod.gunterdiscord.utils.irc.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
class IRCUtils{
	private static final Logger LOGGER = LoggerFactory.getLogger(IRCUtils.class);
	
	@Nonnull
	static Optional<IRCEvent> buildEvent(@Nonnull String message){
		if(message.equals("PING :tmi.twitch.tv")){
			return Optional.of(new PingIRCEvent());
		}
		if(!message.startsWith(":")){
			LOGGER.warn("IRC message doesn't start with ':' => {}", message);
			return Optional.empty();
		}
		message = message.substring(1);
		final var columnIndex = message.indexOf(':');
		final var infos = message.substring(0, Objects.equals(columnIndex, -1) ? message.length() : columnIndex).split(" ");
		final var user = new IRCUser(infos[0]);
		final var eventType = infos[1];
		switch(eventType){
			case "JOIN":
				return Optional.of(new ChannelJoinedIRCEvent(user, eventType, infos[2]));
			case "PART":
				return Optional.of(new ChannelLeftIRCEvent(user, eventType, infos[2]));
			//noinspection SpellCheckingInspection
			case "PRIVMSG":
				if(columnIndex < 0){
					LOGGER.error("Invalid IRC message (no message): {}", message);
					return Optional.empty();
				}
				return Optional.of(new ChannelMessageIRCEvent(user, eventType, infos[2], message.substring(columnIndex + 1)));
			case "PING":
				return Optional.of(new PingIRCEvent());
			case "001":
			case "002":
			case "003":
			case "004":
			case "353":
			case "366":
			case "372":
			case "375":
			case "376":
				break;
			case "421":
				LOGGER.warn("Unknown command: {}", message);
			default:
				LOGGER.warn("Unknown IRC event type {} in {}", eventType, message);
		}
		return Optional.empty();
	}
}
