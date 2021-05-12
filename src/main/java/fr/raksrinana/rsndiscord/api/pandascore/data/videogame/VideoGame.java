package fr.raksrinana.rsndiscord.api.pandascore.data.videogame;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class VideoGame {
	@JsonProperty("id")
	private int id;
	@JsonProperty("name")
	@NotNull
	private String name;
	@JsonProperty("slug")
	@NotNull
	private String slug;
}
