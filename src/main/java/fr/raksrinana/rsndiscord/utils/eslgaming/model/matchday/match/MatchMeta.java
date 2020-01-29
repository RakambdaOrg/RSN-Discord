package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.UnknownDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class MatchMeta{
	@JsonProperty("key")
	private String key;
	@JsonProperty("name")
	private String name;
	@JsonProperty("horizontalPosition")
	private int horizontalPosition;
	@JsonProperty("matchMetaDataType")
	@JsonDeserialize(using = UnknownDeserializer.class)
	private Object matchMetaDataType;
}
