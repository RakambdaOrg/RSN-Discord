package fr.raksrinana.rsndiscord.api.pandascore.data.opponent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import static fr.raksrinana.rsndiscord.api.pandascore.data.opponent.OpponentType.PLAYER;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class PlayerOpponent extends Opponent{
	@JsonProperty("birth_year")
	@Nullable
	private Integer birthYear;
	@JsonProperty("birthday")
	@Nullable
	private String birthday;
	@JsonProperty("first_name")
	@Nullable
	private String firstname;
	@JsonProperty("hometown")
	@Nullable
	private String hometown;
	@JsonProperty("last_name")
	@Nullable
	private String lastname;
	@JsonProperty("nationality")
	@Nullable
	private String nationality;
	@JsonProperty("role")
	@Nullable
	private String role;
	
	@Override
	public OpponentType getType(){
		return PLAYER;
	}
}
