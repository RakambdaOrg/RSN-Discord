package fr.raksrinana.rsndiscord.api.pandascore.data.game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum GameStatus{
	NOT_STARTED,
	NOT_PLAYED,
	RUNNING,
	FINISHED;
	
	@JsonCreator
	@Nullable
	public GameStatus getByName(@Nullable String name){
		for(var value : GameStatus.values()){
			if(value.name().equalsIgnoreCase(name)){
				return value;
			}
		}
		return null;
	}
}
