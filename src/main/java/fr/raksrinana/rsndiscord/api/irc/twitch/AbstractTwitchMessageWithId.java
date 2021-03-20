package fr.raksrinana.rsndiscord.api.irc.twitch;

import fr.raksrinana.rsndiscord.api.irc.messages.IIRCMessage;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractTwitchMessageWithId implements IIRCMessage{
	@NotNull
	public Optional<TwitchMessageId> getMessageId(){
		return getTags().stream()
				.filter(tag -> Objects.equals("msg-id", tag.key()))
				.map(tag -> TwitchMessageId.getFromName(tag.value()))
				.findFirst();
	}
	
	@NotNull
	public abstract Collection<IRCTag> getTags();
}
