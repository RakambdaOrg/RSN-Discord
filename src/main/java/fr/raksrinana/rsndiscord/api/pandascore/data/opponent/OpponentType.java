package fr.raksrinana.rsndiscord.api.pandascore.data.opponent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum OpponentType{
	PLAYER,
	TEAM;
	
	@JsonCreator
	@Nullable
	public OpponentType getByName(@Nullable String name){
		for(var value : OpponentType.values()){
			if(value.name().equalsIgnoreCase(name)){
				return value;
			}
		}
		return null;
	}
}
