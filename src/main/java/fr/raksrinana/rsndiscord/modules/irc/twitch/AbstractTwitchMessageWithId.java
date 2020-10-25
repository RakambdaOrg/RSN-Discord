package fr.raksrinana.rsndiscord.modules.irc.twitch;

import fr.raksrinana.rsndiscord.modules.irc.messages.IIRCMessage;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractTwitchMessageWithId implements IIRCMessage{
	public Optional<TwitchMessageId> getMessageId(){
		return getTags().stream().filter(t -> Objects.equals("msg-id", t.getKey())).map(t -> TwitchMessageId.getFromName(t.getValue())).findFirst();
	}
	
	public abstract Collection<IRCTag> getTags();
}
