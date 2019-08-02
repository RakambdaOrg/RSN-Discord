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
	
	OverwatchConclusionStrategy(final String strategyName){
		this(strategyName, (s, match) -> "");
	}
	
	OverwatchConclusionStrategy(final String strategyName, final BiFunction<OverwatchConclusionStrategy, OverwatchMatch, String> matchFunction){
		this.strategyName = strategyName;
		this.matchFunction = matchFunction;
	}
	
	@JsonCreator
	@Nonnull
	public OverwatchConclusionStrategy getFromString(@Nonnull final String value){
		try{
			return OverwatchConclusionStrategy.valueOf(value);
		}
		catch(final IllegalArgumentException e){
			LOGGER.warn("Unknown conclusion strategy {}", value);
		}
		return UNKNOWN;
	}
	
	@Nonnull
	public String asString(@Nonnull final OverwatchMatch match){
		return this.matchFunction.apply(this, match);
	}
}
