package fr.raksrinana.rsndiscord.api.pandascore.data.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.api.pandascore.data.opponent.OpponentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class GameWinner{
	@JsonProperty("id")
	@Nullable
	private Integer id;
	@JsonProperty("type")
	@Nullable
	private OpponentType type;
}
