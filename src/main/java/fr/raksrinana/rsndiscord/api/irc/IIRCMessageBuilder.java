package fr.raksrinana.rsndiscord.api.irc;

import fr.raksrinana.rsndiscord.api.irc.messages.IIRCMessage;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

public interface IIRCMessageBuilder{
	@NotNull
	Optional<IIRCMessage> buildEvent(@NotNull String ircMessage);
}
