package fr.raksrinana.rsndiscord.utils.overwatch.year2019.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.match.OverwatchMatch;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.BiFunction;

public enum OverwatchConclusionStrategy{
	@JsonEnumDefaultValue UNKNOWN("UNKNOWN"), MINIMUM("MINIMUM", (s, match) -> {
		if(match.getConclusionValue() > 0){
			return match.getConclusionValue() + " map set";
		}
		return s.name();
	}), BEST_OF("Best of", (s, match) -> {
		if(match.getBestOf() > 0){
			return s.strategyName + " " + match.getBestOf();
		}
		return s.name();
	}), FIRST_TO("First to", (s, match) -> {
		if(match.getConclusionValue() > 0){
			return "First to " + match.getConclusionValue();
		}
		return s.name();
	});
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
	@NonNull
	public OverwatchConclusionStrategy getFromString(@NonNull final String value){
		try{
			return OverwatchConclusionStrategy.valueOf(value);
		}
		catch(final IllegalArgumentException e){
			LOGGER.warn("Unknown conclusion strategy {}", value);
		}
		return UNKNOWN;
	}
	
	@NonNull
	public String asString(@NonNull final OverwatchMatch match){
		return this.matchFunction.apply(this, match);
	}
}
