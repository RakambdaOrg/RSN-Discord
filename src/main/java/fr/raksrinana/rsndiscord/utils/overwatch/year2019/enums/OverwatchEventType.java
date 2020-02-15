package fr.raksrinana.rsndiscord.utils.overwatch.year2019.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;

public enum OverwatchEventType{
	@JsonEnumDefaultValue UNKNOWN, VENUE;
	
	@JsonCreator
	@NonNull
	public OverwatchEventType getFromString(@NonNull final String value){
		try{
			return OverwatchEventType.valueOf(value);
		}
		catch(final IllegalArgumentException e){
			Log.getLogger(null).warn("Unknown event type {}", value);
		}
		return UNKNOWN;
	}
}
