package fr.raksrinana.rsndiscord.api.pandascore.data.league;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.converter.ISO8601ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.converter.URLDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class League{
	@JsonProperty("id")
	private int id;
	@JsonProperty("image_url")
	@JsonDeserialize(using = URLDeserializer.class)
	@Nullable
	private URL imageUrl;
	@JsonProperty("modified_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime modifiedAt;
	@JsonProperty("name")
	@NotNull
	private String name;
	@JsonProperty("slug")
	@NotNull
	private String slug;
	@JsonProperty("url")
	@JsonDeserialize(using = URLDeserializer.class)
	@Nullable
	private URL url;
	
	@NotNull
	public Optional<String> getImageUrlAsString(){
		return Optional.ofNullable(getImageUrl())
				.map(URL::toString);
	}
	
	@NotNull
	public Optional<String> getUrlAsString(){
		return Optional.ofNullable(getUrl()).map(URL::toString);
	}
}
