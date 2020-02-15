package fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import lombok.Getter;
import java.net.URL;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class OverwatchBroadcastChannel{
	@JsonProperty("id")
	private String id;
	@JsonProperty("url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL url;
	
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
		OverwatchBroadcastChannel that = (OverwatchBroadcastChannel) o;
		return Objects.equals(id, that.id);
	}
}
