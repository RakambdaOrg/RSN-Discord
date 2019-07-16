package fr.mrcraftcod.gunterdiscord.utils.overwatch.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;

public enum OverwatchCompetitorStatus{
	UNKNOWN, NORMAL;
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchCompetitorStatus.class);
	
	@JsonCreator
	@Nonnull
	public OverwatchCompetitorStatus getFromString(@Nonnull final String value){
		try{
			return OverwatchCompetitorStatus.valueOf(value);
		}
		catch(IllegalArgumentException e){
			LOGGER.warn("Unknown competitor reason {}", value);
		}
		return UNKNOWN;
	}
}
