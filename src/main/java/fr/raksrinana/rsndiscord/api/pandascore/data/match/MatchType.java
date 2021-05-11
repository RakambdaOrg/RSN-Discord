package fr.raksrinana.rsndiscord.api.pandascore.data.match;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@RequiredArgsConstructor
public enum MatchType {
	BEST_OF("Best of"),
	CUSTOM("Custom"),
	FIRST_TO("First to"),
	OW_BEST_OF("OW Best of");
	
	private final String value;
	
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
