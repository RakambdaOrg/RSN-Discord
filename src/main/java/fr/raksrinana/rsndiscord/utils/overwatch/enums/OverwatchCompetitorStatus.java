package fr.raksrinana.rsndiscord.utils.overwatch.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum OverwatchCompetitorStatus{
	@JsonEnumDefaultValue UNKNOWN, NORMAL;
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchCompetitorStatus.class);
	
	@JsonCreator
	@NonNull
	public OverwatchCompetitorStatus getFromString(@NonNull final String value){
		try{
			return OverwatchCompetitorStatus.valueOf(value);
		}
		catch(final IllegalArgumentException e){
			LOGGER.warn("Unknown competitor reason {}", value);
		}
		return UNKNOWN;
	}
}
