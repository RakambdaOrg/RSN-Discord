package fr.mrcraftcod.gunterdiscord.utils;

import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.List;
import static java.time.temporal.ChronoUnit.*;

public class GunterDuration implements TemporalAmount, Comparable<GunterDuration>{
	private final Duration duration;
	
	private static class DurationUnits{
		static final List<TemporalUnit> UNITS = List.of(SECONDS, NANOS, DAYS);
	}
	
	public GunterDuration(Duration duration){
		this.duration = duration;
	}
	
	@Override
	public int compareTo(@NotNull GunterDuration o){
		return this.duration.compareTo(o.duration);
	}
	
	@Override
	public long get(TemporalUnit unit){
		if(unit == SECONDS || unit == NANOS){
			return duration.get(unit);
		}
		else if(unit == DAYS){
			return duration.getSeconds() / unit.getDuration().getSeconds();
		}
		else{
			throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
		}
	}
	
	@Override
	public List<TemporalUnit> getUnits(){
		return DurationUnits.UNITS;
	}
	
	@Override
	public Temporal addTo(Temporal temporal){
		return duration.addTo(temporal);
	}
	
	@Override
	public Temporal subtractFrom(Temporal temporal){
		return duration.subtractFrom(temporal);
	}
}
