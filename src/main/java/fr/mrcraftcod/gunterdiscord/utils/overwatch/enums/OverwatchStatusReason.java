package fr.mrcraftcod.gunterdiscord.utils.overwatch.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

public enum OverwatchStatusReason{
	@JsonEnumDefaultValue UNKNOWN, NORMAL;
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchStatusReason.class);
	
	@JsonCreator
	@Nonnull
	public OverwatchStatusReason getFromString(@Nonnull final String value){
		try{
			return OverwatchStatusReason.valueOf(value);
		}
		catch(final IllegalArgumentException e){
			LOGGER.warn("Unknown status reason {}", value);
		}
		return UNKNOWN;
	}
}
