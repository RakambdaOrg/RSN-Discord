package fr.raksrinana.rsndiscord.modules.irc.twitch;

import fr.raksrinana.rsndiscord.modules.irc.messages.IIRCMessage;
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
