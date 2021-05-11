package fr.raksrinana.rsndiscord.api.pandascore.data.opponent;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = WrappedPlayerOpponent.class, name = "Player"),
		@JsonSubTypes.Type(value = WrappedTeamOpponent.class, name = "Team")
})
@Getter
public abstract class WrappedOpponent<T extends Opponent>{
	private final OpponentType type;
	
	@JsonProperty("opponent")
	private T opponent;
	
	public WrappedOpponent(OpponentType type){this.type = type;}
}
