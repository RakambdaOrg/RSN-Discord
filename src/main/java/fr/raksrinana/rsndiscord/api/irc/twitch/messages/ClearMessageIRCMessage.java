package fr.raksrinana.rsndiscord.api.irc.twitch.messages;

import fr.raksrinana.rsndiscord.api.irc.messages.IIRCMessage;
import fr.raksrinana.rsndiscord.api.irc.twitch.IRCTag;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public record ClearMessageIRCMessage(@NotNull List<IRCTag> tags,
                                     @NotNull String ircChannel,
                                     @NotNull String message) implements IIRCMessage{
}
