package fr.raksrinana.rsndiscord.modules.irc;

import fr.raksrinana.rsndiscord.modules.irc.messages.IIRCMessage;
import java.util.Optional;

public interface IIRCMessageBuilder{
	Optional<IIRCMessage> buildEvent(String ircMessage);
}
