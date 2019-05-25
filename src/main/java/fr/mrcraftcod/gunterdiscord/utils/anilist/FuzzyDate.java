package fr.mrcraftcod.gunterdiscord.utils.anilist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import fr.mrcraftcod.gunterdiscord.utils.GunterDuration;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-12.
 *
 * @author Thomas Couchoud
 * @since 2018-10-12
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FuzzyDate{
	private final Calendar calendar;
	private boolean isSet;
	
	private FuzzyDate(){
		this.calendar = Calendar.getInstance();
		this.isSet = false;
	}
	
	public Optional<GunterDuration> durationTo(final Date toDate){
		return this.asDate().map(date -> new GunterDuration(Duration.between(date.toInstant(), toDate.toInstant())));
	}
	
	public Optional<Date> asDate(){
		return this.isSet() ? Optional.of(this.calendar.getTime()) : Optional.empty();
	}
	
	public boolean isSet(){
		return this.isSet;
	}
	
	@JsonSetter("day")
	private void setDay(final Integer day){
		if(Objects.nonNull(day)){
			this.isSet = true;
			this.calendar.set(Calendar.DAY_OF_MONTH, day);
		}
	}
	
	@JsonSetter("month")
	private void setMonth(final Integer month){
		if(Objects.nonNull(month)){
			this.isSet = true;
			this.calendar.set(Calendar.MONTH, month - 1);
		}
	}
	
	@JsonSetter("year")
	private void setYear(final Integer year){
		if(Objects.nonNull(year)){
			this.isSet = true;
			this.calendar.set(Calendar.YEAR, year);
		}
	}
}
