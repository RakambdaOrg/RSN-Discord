package fr.mrcraftcod.gunterdiscord.utils.overwatch.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

public enum OverwatchConclusionStrategy{
	UNKNOWN, MINIMUM, BEST_OF, FIRST_TO;
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchConclusionStrategy.class);
	
	@JsonCreator
	@Nonnull
	public OverwatchConclusionStrategy getFromString(@Nonnull final String value){
		try{
			OverwatchConclusionStrategy.valueOf(value);
		}
		catch(IllegalArgumentException e){
			LOGGER.warn("Unknown conclusion strategy {}", value);
		}
		return UNKNOWN;
	}
}
