package fr.rakambda.rsndiscord.spring.api.anilist.response.gql;

import fr.rakambda.rsndiscord.spring.util.DayDuration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDate;
import java.util.Optional;
import static java.time.Duration.between;
import static java.util.Objects.nonNull;
import static java.util.Optional.empty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FuzzyDate{
	private Integer day;
	private Integer month;
	private Integer year;
	
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
