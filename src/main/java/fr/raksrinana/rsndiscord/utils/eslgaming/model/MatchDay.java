package fr.raksrinana.rsndiscord.utils.eslgaming.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.Match;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateDeserializer;
import fr.raksrinana.rsndiscord.utils.json.SQLTimestampDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class MatchDay{
	@JsonProperty("tsnow")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private ZonedDateTime tsnow;
	@JsonProperty("date")
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate date;
	@JsonProperty("date_matchday")
	private String dateMatchDay;
	@JsonProperty("date_ts")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private ZonedDateTime dateTs;
	@JsonProperty("matchday")
	private int matchDay;
	@JsonProperty("matchdaybegin")
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate matchDayBegin;
	@JsonProperty("nextday")
	private boolean nextDay;
	@JsonProperty("today")
	private boolean today;
	@JsonProperty("matches")
	private List<Match> matches;
	
	@Override
	public int hashCode(){
		return Objects.hash(getDateMatchDay());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof MatchDay)){
			return false;
		}
		MatchDay that = (MatchDay) o;
		return Objects.equals(getDateMatchDay(), that.getDateMatchDay());
	}
	
	@Override
	public String toString(){
		return getDateMatchDay() + '(' + getMatches().size() + ')';
	}
}
