package fr.raksrinana.rsndiscord.api.anilist.data;

import lombok.NonNull;
import java.time.ZonedDateTime;

public interface IAniListDatedObject extends IAniListObject{
	@NonNull ZonedDateTime getDate();
}
