package fr.raksrinana.rsndiscord.api.pandascore.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum MatchType {
	BEST_OF,
	CUSTOM,
	FIRST_TO,
	OW_BEST_OF;
	
	@JsonCreator
	@Nullable
	public MatchType getByName(@Nullable String name) {
		for (var type : MatchType.values()) {
			if (type.name().equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}
}
