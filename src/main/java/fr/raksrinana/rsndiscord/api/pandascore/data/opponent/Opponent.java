package fr.raksrinana.rsndiscord.api.pandascore.data.opponent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public abstract class Opponent {
	@JsonProperty("id")
	private int id;
	@JsonProperty("image_url")
	@JsonDeserialize(using = URLDeserializer.class)
	@Nullable
	private URL imageUrl;
	@JsonProperty("name")
	@NotNull
	private String name;
	@JsonProperty("slug")
	@Nullable
	private String slug;
	
	@NotNull
	public abstract String getCompleteName();
	
	@NotNull
	public abstract OpponentType getType();
	
	@NotNull
	public Optional<String> getImageUrlAsString(){
		return Optional.ofNullable(getImageUrl())
				.map(URL::toString);
	}
}
