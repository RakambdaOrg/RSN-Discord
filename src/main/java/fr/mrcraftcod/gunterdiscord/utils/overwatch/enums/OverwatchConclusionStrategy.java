package fr.mrcraftcod.gunterdiscord.utils.overwatch.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match.OverwatchMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public enum OverwatchConclusionStrategy{
	UNKNOWN("UNKNOWN"), MINIMUM("MINIMUM", (s, match) -> {
		if(match.getConclusionValue() > 0){
			return match.getConclusionValue() + " map set";
		}
		return s.name();
	}), BEST_OF("Best of", (s, match) -> {
		if(match.getBestOf() > 0){
			return s.strategyName + " " + match.getBestOf();
		}
		return s.name();
	}), FIRST_TO("First to");
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchConclusionStrategy.class);
	private final String strategyName;
	private final BiFunction<OverwatchConclusionStrategy, OverwatchMatch, String> matchFunction;
	
	OverwatchConclusionStrategy(String strategyName){
		this(strategyName, (s, match) -> "");
	}
	
	OverwatchConclusionStrategy(String strategyName, BiFunction<OverwatchConclusionStrategy, OverwatchMatch, String> matchFunction){
		this.strategyName = strategyName;
		this.matchFunction = matchFunction;
	}
	
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
	
	@Nonnull
	public String asString(@Nonnull OverwatchMatch match){
		return matchFunction.apply(this, match);
	}
}
