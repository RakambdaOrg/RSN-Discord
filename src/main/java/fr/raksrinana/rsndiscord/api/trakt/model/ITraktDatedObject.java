package fr.raksrinana.rsndiscord.api.trakt.model;

import lombok.NonNull;
import java.time.ZonedDateTime;

public interface ITraktDatedObject extends ITraktObject{
	@NonNull ZonedDateTime getDate();
}
