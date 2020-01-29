package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote.contestant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote.contestant.gameresult.GameResultKey;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class GameResult{
	@JsonProperty("key")
	private GameResultKey key;
}
