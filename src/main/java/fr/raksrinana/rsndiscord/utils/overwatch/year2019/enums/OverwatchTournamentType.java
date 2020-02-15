package fr.raksrinana.rsndiscord.utils.overwatch.year2019.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum OverwatchTournamentType{
	@JsonEnumDefaultValue UNKNOWN, OPEN_MATCHES, PLAYOFFS;
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchTournamentType.class);
	
	@JsonCreator
	@NonNull
	public OverwatchTournamentType getFromString(@NonNull final String value){
		try{
			return OverwatchTournamentType.valueOf(value);
		}
		catch(final IllegalArgumentException e){
			LOGGER.warn("Unknown tournament type {}", value);
		}
		return UNKNOWN;
	}
}
