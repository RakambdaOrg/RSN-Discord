package fr.raksrinana.rsndiscord.api.anilist.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.DayDuration;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDate;
import java.util.Optional;
import static java.time.Duration.between;
import static java.util.Objects.nonNull;
import static java.util.Optional.empty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class FuzzyDate{
	private static final String QUERY = "{year month day}";
	
	@JsonProperty("day")
	private Integer day;
	@JsonProperty("month")
	private Integer month;
	@JsonProperty("year")
	private Integer year;
	
	public static String getQuery(String fieldName){
		return fieldName + QUERY;
	}
	
	@NotNull
	public Optional<DayDuration> durationTo(@NotNull LocalDate toDate){
		return asDate().map(date -> new DayDuration(between(date.atStartOfDay(), toDate.atStartOfDay())));
	}
	
	@NotNull
	public Optional<LocalDate> asDate(){
		return nonNull(day) && nonNull(month) && nonNull(year)
				? Optional.of(LocalDate.of(year, month, day))
				: empty();
	}
}
