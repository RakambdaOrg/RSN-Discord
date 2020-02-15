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
public class OverwatchVideo{
	@JsonProperty("name")
	private String name;
	@JsonProperty("description")
	private String description;
	@JsonProperty("videoLink")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL videoLink;
	@JsonProperty("youtubeId")
	private String youtubeId;
	@JsonProperty("thumbnailUrl")
	private URL thumbnailUrl;
	
	@Override
	public int hashCode(){
		return Objects.hash(videoLink);
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		OverwatchVideo that = (OverwatchVideo) o;
		return Objects.equals(videoLink, that.videoLink);
	}
}
