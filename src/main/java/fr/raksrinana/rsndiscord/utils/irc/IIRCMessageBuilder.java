package fr.raksrinana.rsndiscord.utils.irc;

import fr.raksrinana.rsndiscord.utils.irc.messages.IRCMessage;
import java.util.Optional;

public interface IIRCMessageBuilder{
	Optional<IRCMessage> buildEvent(String ircMessage);
}
