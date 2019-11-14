package fr.raksrinana.rsndiscord.utils.anilist;

import lombok.NonNull;
import java.time.LocalDateTime;

public interface DatedObject extends AniListObject{
	@NonNull LocalDateTime getDate();
}
