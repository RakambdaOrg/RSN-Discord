package fr.raksrinana.rsndiscord.utils.overwatch.year2019.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum OverwatchAdvantageComparing{
	@JsonEnumDefaultValue UNKNOWN, AFTER_MATCH;
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchAdvantageComparing.class);
	
	@JsonCreator
	@NonNull
	public OverwatchAdvantageComparing getFromString(@NonNull final String value){
		try{
			return OverwatchAdvantageComparing.valueOf(value);
		}
		catch(final IllegalArgumentException e){
			LOGGER.warn("Unknown advantage comparing {}", value);
		}
		return UNKNOWN;
	}
}
