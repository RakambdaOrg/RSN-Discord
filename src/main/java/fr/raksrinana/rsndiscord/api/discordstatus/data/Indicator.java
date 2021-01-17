package fr.raksrinana.rsndiscord.api.discordstatus.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import java.awt.Color;
import static java.awt.Color.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum Indicator{
	CRITICAL(RED),
	MAJOR(ORANGE),
	MINOR(YELLOW),
	NONE(BLACK);
	
	private final Color color;
	
	Indicator(Color color){
		this.color = color;
	}
	
	@JsonCreator
	@Nullable
	public Indicator getByName(@Nullable String name){
		for(var indicator : Indicator.values()){
			if(indicator.name().equalsIgnoreCase(name)){
				return indicator;
			}
		}
		return null;
	}
}
