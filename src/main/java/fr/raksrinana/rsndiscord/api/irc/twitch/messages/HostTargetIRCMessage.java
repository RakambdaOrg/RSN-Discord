package fr.raksrinana.rsndiscord.api.irc.twitch.messages;

import fr.raksrinana.rsndiscord.api.irc.messages.IIRCMessage;
import org.jetbrains.annotations.NotNull;

public record HostTargetIRCMessage(@NotNull String ircChannel,
                                   @NotNull String infos) implements IIRCMessage{
}
