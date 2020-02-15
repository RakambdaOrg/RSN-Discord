package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.utils.team;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.utils.Link;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Team{
	@JsonProperty("teamId")
	private int teamId;
	@JsonProperty("longName")
	private List<String> longName;
	@JsonProperty("shortName")
	private String shortName;
	@JsonProperty("link")
	private Link link;
	@JsonProperty("teamLogo")
	private Set<Logo> teamLogo;
	
	@Override
	public int hashCode(){
		return Objects.hash(getTeamId());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof Team)){
			return false;
		}
		Team team = (Team) o;
		return getTeamId() == team.getTeamId();
	}
}
