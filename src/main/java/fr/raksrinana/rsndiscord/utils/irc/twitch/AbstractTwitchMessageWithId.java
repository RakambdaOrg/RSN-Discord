package fr.raksrinana.rsndiscord.utils.irc.twitch;

import fr.raksrinana.rsndiscord.utils.irc.messages.IRCMessage;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractTwitchMessageWithId implements IRCMessage{
	public Optional<TwitchMessageId> getMessageId(){
		return getTags().stream().filter(t -> Objects.equals("msg-id", t.getKey())).map(t -> TwitchMessageId.getFromName(t.getValue())).findFirst();
	}
	
	public abstract Collection<IRCTag> getTags();
}
