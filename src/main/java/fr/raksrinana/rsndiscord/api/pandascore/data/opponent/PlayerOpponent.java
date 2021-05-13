package fr.raksrinana.rsndiscord.api.pandascore.data.opponent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Locale;
import java.util.Objects;
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
	public @NotNull String getCompleteName(){
		var sb = new StringBuilder();
		
		if(Objects.nonNull(nationality)){
			sb.append(":flag_").append(nationality.toLowerCase(Locale.ROOT)).append(": ");
		}
		
		sb.append(getName());
		
		if(Objects.nonNull(getFirstname()) && Objects.nonNull(getLastname())){
			sb.append(" (").append(getFirstname())
					.append(" ").append(getLastname()).append(")");
		}
		
		return sb.toString();
	}
	
	@Override
	public @NotNull OpponentType getType(){
		return PLAYER;
	}
}
