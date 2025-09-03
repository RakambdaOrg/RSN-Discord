package fr.rakambda.rsndiscord.spring.util;

import org.jspecify.annotations.NonNull;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.List;
import java.util.Objects;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Wraps a {@link Duration} and allow to perform a {@link Duration#get(TemporalUnit)} with {@link java.time.temporal.ChronoUnit#DAYS}.
 */
public record DayDuration(@NonNull Duration duration)
		implements TemporalAmount, Comparable<DayDuration>{
	static final List<TemporalUnit> UNITS = List.of(SECONDS, NANOS, DAYS);
	
	@Override
	public int compareTo(@NonNull DayDuration o){
		return duration.compareTo(o.duration);
	}
	
	@Override
	public long get(@NonNull TemporalUnit unit){
		if(Objects.equals(unit, SECONDS) || Objects.equals(unit, NANOS)){
			return duration.get(unit);
		}
		else if(Objects.equals(unit, DAYS)){
			return duration.getSeconds() / unit.getDuration().getSeconds();
		}
		else{
			throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
		}
	}
	
	@Override
	@NonNull
	public List<TemporalUnit> getUnits(){
		return UNITS;
	}
	
	@Override
	@NonNull
	public Temporal addTo(@NonNull Temporal temporal){
		return duration.addTo(temporal);
	}
	
	@Override
	@NonNull
	public Temporal subtractFrom(@NonNull Temporal temporal){
		return duration.subtractFrom(temporal);
	}
}
