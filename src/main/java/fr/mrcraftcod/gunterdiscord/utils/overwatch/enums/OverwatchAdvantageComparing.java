package fr.mrcraftcod.gunterdiscord.utils.overwatch.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

public enum OverwatchAdvantageComparing{
	UNKNOWN, AFTER_MATCH;
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchAdvantageComparing.class);
	
	@JsonCreator
	@Nonnull
	public OverwatchAdvantageComparing getFromString(@Nonnull final String value){
		try{
			return OverwatchAdvantageComparing.valueOf(value);
		}
		catch(IllegalArgumentException e){
			LOGGER.warn("Unknown advantage comparing {}", value);
		}
		return UNKNOWN;
	}
}
