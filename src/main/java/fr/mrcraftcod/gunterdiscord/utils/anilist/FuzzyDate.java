package fr.mrcraftcod.gunterdiscord.utils.anilist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.utils.GunterDuration;
import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.LocalDate;
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
	@JsonProperty("day")
	private int day;
	@JsonProperty("month")
	private int month;
	@JsonProperty("year")
	private int year;
	private boolean isSet;
	
	private FuzzyDate(){
		this.isSet = false;
	}
	
	@Nonnull
	public Optional<GunterDuration> durationTo(@Nonnull final LocalDate toDate){
		return this.asDate().map(date -> new GunterDuration(Duration.between(date.atStartOfDay(), toDate.atStartOfDay())));
	}
	
	@Nonnull
	public Optional<LocalDate> asDate(){
		return this.isSet() ? Optional.of(LocalDate.of(this.year, this.month, this.day)) : Optional.empty();
	}
	
	public boolean isSet(){
		return this.isSet;
	}
}
