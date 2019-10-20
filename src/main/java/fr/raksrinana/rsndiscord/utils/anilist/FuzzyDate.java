package fr.raksrinana.rsndiscord.utils.anilist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.GunterDuration;
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
	@JsonProperty("day")
	private Integer day;
	@JsonProperty("month")
	private Integer month;
	@JsonProperty("year")
	private Integer year;
	
	private FuzzyDate(){
	}
	
	@Nonnull
	public Optional<GunterDuration> durationTo(@Nonnull final LocalDate toDate){
		return this.asDate().map(date -> new GunterDuration(Duration.between(date.atStartOfDay(), toDate.atStartOfDay())));
	}
	
	@Nonnull
	public Optional<LocalDate> asDate(){
		return (Objects.nonNull(this.day) && Objects.nonNull(this.month) && Objects.nonNull(this.year)) ? Optional.of(LocalDate.of(this.year, this.month, this.day)) : Optional.empty();
	}
}
