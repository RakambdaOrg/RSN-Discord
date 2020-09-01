package fr.raksrinana.rsndiscord.utils.discordstatus.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum Status{
	INVESTIGATING, IDENTIFIED, MONITORING, RESOLVED, POSTMORTEM;
	
	@JsonCreator
	public Status getByName(String name){
		for(var indicator : Status.values()){
			if(indicator.name().equalsIgnoreCase(name)){
				return indicator;
			}
		}
		return null;
	}
}
