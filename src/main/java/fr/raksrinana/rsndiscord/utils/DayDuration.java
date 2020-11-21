package fr.raksrinana.rsndiscord.utils;

import lombok.NonNull;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.List;
import java.util.Objects;
import static java.time.temporal.ChronoUnit.*;

/**
 * Wraps a {@link Duration} and allow to perform a {@link Duration#get(TemporalUnit)} with {@link java.time.temporal.ChronoUnit#DAYS}.
 */
public class DayDuration implements TemporalAmount, Comparable<DayDuration>{
	static final List<TemporalUnit> UNITS = List.of(SECONDS, NANOS, DAYS);
	private final Duration duration;
	
	/**
	 * Create a day duration from a duration.
	 *
	 * @param duration The duration to wrap.
	 */
	public DayDuration(@NonNull final Duration duration){
		this.duration = duration;
	}
	
	@Override
	public int compareTo(@NonNull final DayDuration o){
		return this.duration.compareTo(o.duration);
	}
	
	@Override
	public long get(@NonNull final TemporalUnit unit){
		if(Objects.equals(unit, SECONDS) || Objects.equals(unit, NANOS)){
			return this.duration.get(unit);
		}
		else if(Objects.equals(unit, DAYS)){
			return this.duration.getSeconds() / unit.getDuration().getSeconds();
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
	public Temporal addTo(@NonNull final Temporal temporal){
		return this.duration.addTo(temporal);
	}
	
	@Override
	@NonNull
	public Temporal subtractFrom(@NonNull final Temporal temporal){
		return this.duration.subtractFrom(temporal);
	}
}
