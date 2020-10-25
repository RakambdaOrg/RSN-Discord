package fr.raksrinana.rsndiscord.modules.series.trakt.model;

import lombok.NonNull;
import java.time.ZonedDateTime;

public interface ITraktDatedObject extends ITraktObject{
	@NonNull ZonedDateTime getDate();
}
