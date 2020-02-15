package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event.banner.EventBanner;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event.match.Match;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Event{
	@JsonProperty("eventBanner")
	private EventBanner eventBanner;
	@JsonProperty("matches")
	private Set<Match> matches;
	
	@Override
	public int hashCode(){
		return Objects.hash(getEventBanner());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof Event)){
			return false;
		}
		Event event = (Event) o;
		return Objects.equals(getEventBanner(), event.getEventBanner());
	}
}
