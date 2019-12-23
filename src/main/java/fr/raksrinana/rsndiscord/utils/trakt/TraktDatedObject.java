package fr.raksrinana.rsndiscord.utils.trakt;

import lombok.NonNull;
import java.time.LocalDateTime;

public interface TraktDatedObject extends TraktObject{
	@NonNull LocalDateTime getDate();
}
