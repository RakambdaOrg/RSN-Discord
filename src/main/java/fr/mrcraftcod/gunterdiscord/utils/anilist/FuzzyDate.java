package fr.mrcraftcod.gunterdiscord.utils.anilist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import fr.mrcraftcod.gunterdiscord.utils.GunterDuration;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

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
	
	private FuzzyDate(){this.calendar = Calendar.getInstance();}
	
	public GunterDuration durationTo(final FuzzyDate toDate){
		return new GunterDuration(Duration.between(this.asDate().toInstant(), toDate.asDate().toInstant()));
	}
	
	public Date asDate(){
		return this.calendar.getTime();
	}
	
	@JsonSetter("day")
	private void setDay(final Integer day){
		if(Objects.nonNull(day)){
			this.calendar.set(Calendar.DAY_OF_MONTH, day);
		}
	}
	
	@JsonSetter("month")
	private void setMonth(final Integer month){
		if(Objects.nonNull(month)){
			this.calendar.set(Calendar.MONTH, month);
		}
	}
	
	@JsonSetter("year")
	private void setYear(final Integer year){
		if(Objects.nonNull(year)){
			this.calendar.set(Calendar.YEAR, year);
		}
	}
}
