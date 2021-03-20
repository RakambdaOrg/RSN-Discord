package fr.raksrinana.rsndiscord.api.irc.twitch.messages;

import fr.raksrinana.rsndiscord.api.irc.messages.IIRCMessage;
import fr.raksrinana.rsndiscord.api.irc.twitch.IRCTag;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public record UserStateIRCMessage(@NotNull List<IRCTag> tags,
                                  @NotNull String channel) implements IIRCMessage{
}
