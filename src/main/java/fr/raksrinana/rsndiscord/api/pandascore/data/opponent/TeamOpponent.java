package fr.raksrinana.rsndiscord.api.pandascore.data.opponent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.ISO8601ZonedDateTimeDeserializer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.api.pandascore.data.opponent.OpponentType.TEAM;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class TeamOpponent extends Opponent{
	@JsonProperty("acronym")
	@Nullable
	private String acronym;
	@JsonProperty("location")
	private String location;
	@JsonProperty("modified_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime modifiedAt;
	
	@Override
	public @NotNull String getCompleteName(){
		var sb = new StringBuilder();
		
		if(Objects.nonNull(location)){
			sb.append(":flag_").append(location.toLowerCase(Locale.ROOT)).append(": ");
		}
		
		sb.append(getName());
		
		if(Objects.nonNull(getAcronym())){
			sb.append(" (").append(getAcronym()).append(")");
		}
		
		return sb.toString();
	}
	
	@Override
	public @NotNull OpponentType getType(){
		return TEAM;
	}
}
