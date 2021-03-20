package fr.raksrinana.rsndiscord.api.irc.messages;

import fr.raksrinana.rsndiscord.api.irc.twitch.IRCUser;
import org.jetbrains.annotations.NotNull;

public record ChannelLeftIRCMessage(@NotNull IRCUser user,
                                    @NotNull String channel) implements IIRCMessage{
}
