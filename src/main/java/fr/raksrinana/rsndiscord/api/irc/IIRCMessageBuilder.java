package fr.raksrinana.rsndiscord.api.irc;

import fr.raksrinana.rsndiscord.api.irc.messages.IIRCMessage;
import java.util.Optional;

public interface IIRCMessageBuilder{
	Optional<IIRCMessage> buildEvent(String ircMessage);
}
