package fr.raksrinana.rsndiscord.utils.anilist;

import lombok.NonNull;
import java.time.ZonedDateTime;

public interface AnilistDatedObject extends AniListObject{
	@NonNull ZonedDateTime getDate();
}
