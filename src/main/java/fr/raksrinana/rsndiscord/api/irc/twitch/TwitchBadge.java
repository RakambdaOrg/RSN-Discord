package fr.raksrinana.rsndiscord.api.irc.twitch;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
record TwitchBadge(@NotNull String name,
                   @NotNull String version){
}
