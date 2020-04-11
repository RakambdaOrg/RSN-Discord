package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.generic.GameVersionMap;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote.Action;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote.Contestant;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote.Round;
import fr.raksrinana.rsndiscord.utils.json.ISO8601ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import java.time.ZonedDateTime;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class MapVote implements Comparable<MapVote>{
	@JsonProperty("createdAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime createdAt;
	@JsonProperty("updatedAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime updatedAt;
	@JsonProperty("key")
	private String key;
	@JsonProperty("step")
	private int step;
	@JsonProperty("contestant")
	private Contestant contestant;
	@JsonProperty("action")
	private Action action;
	@JsonProperty("gameVersionMap")
	private GameVersionMap gameVersionMap;
	@JsonProperty("round")
	private Round round;
	@JsonProperty("matchKey")
	private String matchKey;
	
	@Override
	public int compareTo(@NonNull MapVote o){
		if(Objects.equals(getMatchKey(), o.getMatchKey())){
			return getMatchKey().compareTo(o.getMatchKey());
		}
		return Integer.compare(getStep(), o.getStep());
	}
}
