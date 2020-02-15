package fr.raksrinana.rsndiscord.utils.overwatch.year2019.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum OverwatchStatusReason{
	@JsonEnumDefaultValue UNKNOWN, NORMAL;
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchStatusReason.class);
	
	@JsonCreator
	@NonNull
	public OverwatchStatusReason getFromString(@NonNull final String value){
		try{
			return OverwatchStatusReason.valueOf(value);
		}
		catch(final IllegalArgumentException e){
			LOGGER.warn("Unknown status reason {}", value);
		}
		return UNKNOWN;
	}
}
