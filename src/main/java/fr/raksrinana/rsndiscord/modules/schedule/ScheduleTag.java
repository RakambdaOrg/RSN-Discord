package fr.raksrinana.rsndiscord.modules.schedule;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum ScheduleTag{
	ANILIST_AIRING_SCHEDULE,
	DELETE_CHANNEL,
	DELETE_MESSAGE,
	NONE,
	REMOVE_ROLE,
	UNBAN_USER;
	
	@JsonCreator
	@NonNull
	public static ScheduleTag getFromString(@NonNull final String value){
		return ScheduleTag.valueOf(value);
	}
}
