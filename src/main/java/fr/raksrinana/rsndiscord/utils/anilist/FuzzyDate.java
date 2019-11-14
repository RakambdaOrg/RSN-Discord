package fr.raksrinana.rsndiscord.utils.anilist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.DayDuration;
import lombok.NonNull;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FuzzyDate{
	private static final String QUERY = "{year month day}";
	@JsonProperty("day")
	private Integer day;
	@JsonProperty("month")
	private Integer month;
	@JsonProperty("year")
	private Integer year;
	
	private FuzzyDate(){
	}
	
	public static String getQuery(String fieldName){
		return fieldName + QUERY;
	}
	
	@NonNull
	public Optional<DayDuration> durationTo(@NonNull final LocalDate toDate){
		return this.asDate().map(date -> new DayDuration(Duration.between(date.atStartOfDay(), toDate.atStartOfDay())));
	}
	
	@NonNull
	public Optional<LocalDate> asDate(){
		return (Objects.nonNull(this.day) && Objects.nonNull(this.month) && Objects.nonNull(this.year)) ? Optional.of(LocalDate.of(this.year, this.month, this.day)) : Optional.empty();
	}
}
