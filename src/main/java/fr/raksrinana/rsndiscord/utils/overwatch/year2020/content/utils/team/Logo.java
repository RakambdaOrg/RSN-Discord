package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.utils.team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.utils.asset.Asset;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Logo{
	@JsonProperty("teamIconUsage")
	private String teamIconUsage;
	@JsonProperty("teamIconSvg")
	private Asset teamIconSvg;
	@JsonProperty("teamIconPng")
	private Asset teamIconPng;
	
	@Override
	public int hashCode(){
		return Objects.hash(getTeamIconUsage());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof Logo)){
			return false;
		}
		Logo logo = (Logo) o;
		return Objects.equals(getTeamIconUsage(), logo.getTeamIconUsage());
	}
}
