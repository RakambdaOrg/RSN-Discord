package fr.raksrinana.rsndiscord.modules.externaltodos.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Todo{
	@JsonProperty("id")
	private int id;
	@JsonProperty("description")
	private String description;
	@JsonProperty("kind")
	private Kind kind;
	@JsonProperty("can_be_dropped")
	private boolean canBeDropped;
	@JsonProperty("status")
	private Status status;
}
