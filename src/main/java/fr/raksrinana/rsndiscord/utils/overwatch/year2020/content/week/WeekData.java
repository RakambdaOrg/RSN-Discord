package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.ISO8601DateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event.Event;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event.match.Match;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class WeekData{
	@JsonProperty("name")
	private String name;
	// @JsonProperty("sponsor")
	// @JsonDeserialize(using = UnknownDeserializer.class)
	// private Set<Object> sponsor;
	@JsonProperty("presentedBy")
	private String presentedBy;
	@JsonProperty("subtitle")
	private String subtitle;
	@JsonProperty("weekNumber")
	private int weekNumber;
	@JsonProperty("startDate")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime startDate;
	@JsonProperty("endDate")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime endDate;
	@JsonProperty("events")
	private Set<Event> events;
	@JsonProperty("pagination")
	private Pagination pagination;
	
	@Override
	public int hashCode(){
		return Objects.hash(getName());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof WeekData)){
			return false;
		}
		WeekData weekData = (WeekData) o;
		return Objects.equals(getName(), weekData.getName());
	}
	
	public Optional<Match> getCurrentMatch(){
		final var now = LocalDateTime.now();
		return this.getMatches().stream().filter(match -> match.isLive() || (now.isAfter(match.getStartDate()) && now.isBefore(match.getEndDate()))).findFirst();
	}
	
	public List<Match> getMatches(){
		return getEvents().stream().flatMap(event -> event.getMatches().stream()).filter(Objects::nonNull).collect(Collectors.toList());
	}
	
	public int getMatchCount(){
		return getMatches().size();
	}
	
	public Optional<Match> getNextMatch(){
		final var now = LocalDateTime.now();
		return this.getMatches().stream().filter(match -> now.isBefore(match.getStartDate())).min(Comparator.comparing(Match::getStartDate));
	}
}
