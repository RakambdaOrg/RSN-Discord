package fr.raksrinana.rsndiscord.modules.anilist.data;

import lombok.NonNull;
import java.time.ZonedDateTime;

public interface IAniListDatedObject extends IAniListObject{
	@NonNull ZonedDateTime getDate();
}
