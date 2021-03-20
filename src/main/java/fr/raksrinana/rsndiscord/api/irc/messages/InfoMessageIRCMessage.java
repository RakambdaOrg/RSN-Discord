package fr.raksrinana.rsndiscord.api.irc.messages;

import org.jetbrains.annotations.NotNull;

public record InfoMessageIRCMessage(@NotNull String message) implements IIRCMessage{
}
