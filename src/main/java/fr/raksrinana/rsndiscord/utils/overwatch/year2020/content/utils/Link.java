package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.utils;

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
public class Link{
	@JsonProperty("title")
	private String title;
	@JsonProperty("href")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL href;
	
	@Override
	public int hashCode(){
		return Objects.hash(getHref());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof Link)){
			return false;
		}
		Link link = (Link) o;
		return Objects.equals(getHref(), link.getHref());
	}
}
