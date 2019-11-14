package fr.raksrinana.rsndiscord.utils.overwatch.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum OverwatchBracketType{
	@JsonEnumDefaultValue UNKNOWN, SE, OPEN_MATCH, DE;
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchBracketType.class);
	
	@JsonCreator
	@NonNull
	public OverwatchBracketType getFromString(@NonNull final String value){
		try{
			return OverwatchBracketType.valueOf(value);
		}
		catch(final IllegalArgumentException e){
			LOGGER.warn("Unknown bracket type {}", value);
		}
		return UNKNOWN;
	}
}
