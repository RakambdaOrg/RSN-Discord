package fr.mrcraftcod.gunterdiscord.utils.overwatch.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

public enum OverwatchBracketType{
	@JsonEnumDefaultValue UNKNOWN, SE, OPEN_MATCH, DE;
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchBracketType.class);
	
	@JsonCreator
	@Nonnull
	public OverwatchBracketType getFromString(@Nonnull final String value){
		try{
			return OverwatchBracketType.valueOf(value);
		}
		catch(final IllegalArgumentException e){
			LOGGER.warn("Unknown bracket type {}", value);
		}
		return UNKNOWN;
	}
}
