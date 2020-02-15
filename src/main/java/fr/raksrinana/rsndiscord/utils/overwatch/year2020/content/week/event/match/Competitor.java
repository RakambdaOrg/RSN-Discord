package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.net.URL;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Competitor{
	@JsonProperty("id")
	private int id;
	@JsonProperty("handle")
	private String handle;
	@JsonProperty("name")
	private String name;
	@JsonProperty("abbreviatedName")
	private String abbreviatedName;
	@JsonProperty("logo")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL logo;
	@JsonProperty("icon")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL icon;
	
	@Override
	public int hashCode(){
		return Objects.hash(getId());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof Competitor)){
			return false;
		}
		Competitor that = (Competitor) o;
		return getId() == that.getId();
	}
}
