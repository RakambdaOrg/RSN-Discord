package fr.raksrinana.rsndiscord.api.discordstatus.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum IncidentStatus{
	IDENTIFIED,
	INVESTIGATING,
	MONITORING,
	POSTMORTEM,
	RESOLVED;
	
	@JsonCreator
	@Nullable
	public IncidentStatus getByName(String name){
		for(var indicator : IncidentStatus.values()){
			if(indicator.name().equalsIgnoreCase(name)){
				return indicator;
			}
		}
		return null;
	}
}
