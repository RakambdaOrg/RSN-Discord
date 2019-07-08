package fr.mrcraftcod.gunterdiscord.utils.anilist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import fr.mrcraftcod.gunterdiscord.utils.GunterDuration;
import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.LocalDate;
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
	private LocalDate date;
	private boolean isSet;
	
	private FuzzyDate(){
		this.date = LocalDate.now();
		this.isSet = false;
	}
	
	@Nonnull
	public Optional<GunterDuration> durationTo(@Nonnull final LocalDate toDate){
		return this.asDate().map(date -> new GunterDuration(Duration.between(date.atStartOfDay(), toDate.atStartOfDay())));
	}
	
	@Nonnull
	public Optional<LocalDate> asDate(){
		return this.isSet() ? Optional.of(this.date) : Optional.empty();
	}
	
	public boolean isSet(){
		return this.isSet;
	}
	
	@JsonSetter("day")
	private void setDay(final Integer day){
		if(Objects.nonNull(day)){
			this.isSet = true;
			this.date = LocalDate.of(this.date.getYear(), date.getMonth(), day);
		}
	}
	
	@JsonSetter("month")
	private void setMonth(final Integer month){
		if(Objects.nonNull(month)){
			this.isSet = true;
			this.date = LocalDate.of(this.date.getYear(), month, this.date.getDayOfMonth());
		}
	}
	
	@JsonSetter("year")
	private void setYear(final Integer year){
		if(Objects.nonNull(year)){
			this.isSet = true;
			this.date = LocalDate.of(year, this.date.getMonth(), this.date.getDayOfMonth());
		}
	}
}
