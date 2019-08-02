package fr.mrcraftcod.gunterdiscord.utils.overwatch.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

public enum OverwatchTournamentType{
	UNKNOWN, OPEN_MATCHES, PLAYOFFS;
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchTournamentType.class);
	
	@JsonCreator
	@Nonnull
	public OverwatchTournamentType getFromString(@Nonnull final String value){
		try{
			return OverwatchTournamentType.valueOf(value);
		}
		catch(final IllegalArgumentException e){
			LOGGER.warn("Unknown tournament type {}", value);
		}
		return UNKNOWN;
	}
}
