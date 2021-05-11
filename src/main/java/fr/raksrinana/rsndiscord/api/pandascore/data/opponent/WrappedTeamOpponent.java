package fr.raksrinana.rsndiscord.api.pandascore.data.opponent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import static fr.raksrinana.rsndiscord.api.pandascore.data.opponent.OpponentType.TEAM;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("Team")
@Getter
public class WrappedTeamOpponent extends WrappedOpponent<TeamOpponent>{
	public WrappedTeamOpponent(){
		super(TEAM);
	}
}
