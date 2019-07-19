package fr.mrcraftcod.gunterdiscord.utils.overwatch.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

public enum OverwatchStatus{
	UNKNOWN, CONCLUDED, PENDING;
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchStatus.class);
	
	@JsonCreator
	@Nonnull
	public OverwatchStatus getFromString(@Nonnull final String value){
		try{
			return OverwatchStatus.valueOf(value);
		}
		catch(IllegalArgumentException e){
			LOGGER.warn("Unknown status {}", value);
		}
		return UNKNOWN;
	}
}
