package fr.mrcraftcod.gunterdiscord.utils;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.List;
import java.util.Objects;
import static java.time.temporal.ChronoUnit.*;

public class GunterDuration implements TemporalAmount, Comparable<GunterDuration>{
	private final Duration duration;
	
	private static class DurationUnits{
		static final List<TemporalUnit> UNITS = List.of(SECONDS, NANOS, DAYS);
	}
	
	public GunterDuration(@Nonnull final Duration duration){
		this.duration = duration;
	}
	
	@Override
	public int compareTo(@Nonnull final GunterDuration o){
		return this.duration.compareTo(o.duration);
	}
	
	@Override
	public long get(@Nonnull final TemporalUnit unit){
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
	@Nonnull
	public List<TemporalUnit> getUnits(){
		return DurationUnits.UNITS;
	}
	
	@Override
	@Nonnull
	public Temporal addTo(@Nonnull final Temporal temporal){
		return this.duration.addTo(temporal);
	}
	
	@Override
	@Nonnull
	public Temporal subtractFrom(@Nonnull final Temporal temporal){
		return this.duration.subtractFrom(temporal);
	}
}
