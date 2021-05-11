package fr.raksrinana.rsndiscord.api.pandascore.data.match;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@RequiredArgsConstructor
public enum MatchStatus{
	NOT_STARTED(Color.GRAY),
	POSTPONED(Color.ORANGE),
	CANCELLED(Color.RED),
	RUNNING(Color.GREEN),
	FINISHED(Color.BLUE);
	
	private final Color color;
	
	@JsonCreator
	@Nullable
	public MatchStatus getByName(@Nullable String name){
		for(var status : MatchStatus.values()){
			if(status.name().equalsIgnoreCase(name)){
				return status;
			}
		}
		return null;
	}
}
