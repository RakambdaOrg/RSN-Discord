package fr.raksrinana.rsndiscord.api.irc.twitch;

import fr.raksrinana.rsndiscord.api.irc.messages.IIRCMessage;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractTwitchMessageWithId implements IIRCMessage{
	public Optional<TwitchMessageId> getMessageId(){
		return getTags().stream()
				.filter(tag -> Objects.equals("msg-id", tag.getKey()))
				.map(tag -> TwitchMessageId.getFromName(tag.getValue()))
				.findFirst();
	}
	
	public abstract Collection<IRCTag> getTags();
}
