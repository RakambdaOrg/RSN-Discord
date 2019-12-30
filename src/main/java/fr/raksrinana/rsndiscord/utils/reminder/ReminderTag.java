package fr.raksrinana.rsndiscord.utils.reminder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum ReminderTag{
	NONE, ANILIST_AIRING_SCHEDULE;
	
	@JsonCreator
	@NonNull
	public static ReminderTag getFromString(@NonNull final String value){
		return ReminderTag.valueOf(value);
	}
}
