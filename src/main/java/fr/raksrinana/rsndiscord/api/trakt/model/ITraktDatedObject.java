package fr.raksrinana.rsndiscord.api.trakt.model;

import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;

public interface ITraktDatedObject extends ITraktObject{
	@NotNull
	ZonedDateTime getDate();
}
