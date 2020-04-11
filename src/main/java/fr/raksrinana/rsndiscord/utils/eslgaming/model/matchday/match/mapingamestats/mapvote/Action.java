package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote;

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
public enum Action{
	BAN, PICK;
	
	@JsonCreator
	@NonNull
	public static Action getFromString(@NonNull final String value){
		for(final var action : Action.values()){
			if(action.name().equalsIgnoreCase(value)){
				return action;
			}
		}
		throw new IllegalArgumentException("Unknown action " + value);
	}
}
