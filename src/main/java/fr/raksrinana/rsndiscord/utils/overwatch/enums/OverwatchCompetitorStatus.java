package fr.raksrinana.rsndiscord.utils.overwatch.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

public enum OverwatchCompetitorStatus{
	@JsonEnumDefaultValue UNKNOWN, NORMAL;
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchCompetitorStatus.class);
	
	@JsonCreator
	@Nonnull
	public OverwatchCompetitorStatus getFromString(@Nonnull final String value){
		try{
			return OverwatchCompetitorStatus.valueOf(value);
		}
		catch(final IllegalArgumentException e){
			LOGGER.warn("Unknown competitor reason {}", value);
		}
		return UNKNOWN;
	}
}
