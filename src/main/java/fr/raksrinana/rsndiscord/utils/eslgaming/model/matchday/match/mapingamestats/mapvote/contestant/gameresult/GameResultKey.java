package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote.contestant.gameresult;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public enum GameResultKey{
	LOSE, WIN, DRAW, DEFAULTLOSS, DEFAULTWIN;
	
	@JsonCreator
	@NonNull
	public static GameResultKey getFromString(@NonNull final String value){
		for(final var action : GameResultKey.values()){
			if(action.name().equalsIgnoreCase(value)){
				return action;
			}
		}
		throw new IllegalArgumentException("Unknown game result key " + value);
	}
}
