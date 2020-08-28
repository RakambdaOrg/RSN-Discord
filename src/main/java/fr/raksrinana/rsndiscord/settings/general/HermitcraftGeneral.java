package fr.raksrinana.rsndiscord.settings.general;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class HermitcraftGeneral{
	@JsonProperty("notifiedVideos")
	@Setter
	private Set<String> notifiedVideos = new HashSet<>();
	
	public boolean isVideoNotified(String id){
		return notifiedVideos.contains(id);
	}
	
	public void setVideoNotified(String id){
		this.notifiedVideos.add(id);
	}
}
