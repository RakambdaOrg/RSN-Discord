package fr.raksrinana.rsndiscord.modules.anilist.data;

import lombok.NonNull;
import java.time.ZonedDateTime;

public interface IAnilistDatedObject extends IAniListObject{
	@NonNull ZonedDateTime getDate();
}
