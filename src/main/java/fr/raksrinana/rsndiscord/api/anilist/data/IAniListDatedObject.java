package fr.raksrinana.rsndiscord.api.anilist.data;

import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;

public interface IAniListDatedObject extends IAniListObject{
	@NotNull ZonedDateTime getDate();
}
