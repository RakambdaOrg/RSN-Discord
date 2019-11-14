package fr.raksrinana.rsndiscord.utils.overwatch.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class OverwatchGameMode{
	@JsonProperty("Id")
	private String id;
	@JsonProperty("name")
	private String name;
	
	@Override
	public int hashCode(){
		return Objects.hash(id);
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		OverwatchGameMode that = (OverwatchGameMode) o;
		return Objects.equals(id, that.id);
	}
}
