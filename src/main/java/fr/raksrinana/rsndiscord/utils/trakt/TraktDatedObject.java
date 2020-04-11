package fr.raksrinana.rsndiscord.utils.trakt;

import lombok.NonNull;
import java.time.ZonedDateTime;

public interface TraktDatedObject extends TraktObject{
	@NonNull ZonedDateTime getDate();
}
