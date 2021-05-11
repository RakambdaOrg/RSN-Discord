package fr.raksrinana.rsndiscord.api.pandascore.data.match;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Result {
	@JsonProperty("score")
	private int score;
	@JsonProperty("player_id")
	@JsonAlias("team_id")
	private int opponentId;
}
