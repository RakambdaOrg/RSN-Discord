package fr.raksrinana.rsndiscord.utils.overwatch.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

public enum OverwatchState{
	@JsonEnumDefaultValue UNKNOWN("UNKNOWN"), CONCLUDED("Ended"), PENDING("Pending"), IN_PROGRESS("In progress");
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchState.class);
	private final String stateName;
	
	OverwatchState(final String stateName){
		this.stateName = stateName;
	}
	
	@JsonCreator
	@Nonnull
	public OverwatchState getFromString(@Nonnull final String value){
		try{
			return OverwatchState.valueOf(value);
		}
		catch(final IllegalArgumentException e){
			LOGGER.warn("Unknown state {}", value);
		}
		return UNKNOWN;
	}
	
	public String asString(){
		return this.stateName;
	}
}
