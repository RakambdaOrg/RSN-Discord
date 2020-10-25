package fr.raksrinana.rsndiscord.modules.discordstatus.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import java.awt.Color;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum Indicator{
	NONE(Color.BLACK), MINOR(Color.YELLOW), MAJOR(Color.ORANGE), CRITICAL(Color.RED);
	private final Color color;
	
	Indicator(Color color){
		this.color = color;
	}
	
	@JsonCreator
	public Indicator getByName(String name){
		for(var indicator : Indicator.values()){
			if(indicator.name().equalsIgnoreCase(name)){
				return indicator;
			}
		}
		return null;
	}
}
