package fr.raksrinana.rsndiscord.api.pandascore.data.opponent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import static fr.raksrinana.rsndiscord.api.pandascore.data.opponent.OpponentType.PLAYER;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("Player")
@Getter
public class WrappedPlayerOpponent extends WrappedOpponent<PlayerOpponent>{
	public WrappedPlayerOpponent(){
		super(PLAYER);
	}
}
