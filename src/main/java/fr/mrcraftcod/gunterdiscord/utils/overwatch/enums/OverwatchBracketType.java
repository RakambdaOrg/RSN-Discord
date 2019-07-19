package fr.mrcraftcod.gunterdiscord.utils.overwatch.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

public enum OverwatchBracketType{
	UNKNOWN, SE, OPEN_MATCH;
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchBracketType.class);
	
	@JsonCreator
	@Nonnull
	public OverwatchBracketType getFromString(@Nonnull final String value){
		try{
			return OverwatchBracketType.valueOf(value);
		}
		catch(IllegalArgumentException e){
			LOGGER.warn("Unknown bracket type {}", value);
		}
		return UNKNOWN;
	}
}
