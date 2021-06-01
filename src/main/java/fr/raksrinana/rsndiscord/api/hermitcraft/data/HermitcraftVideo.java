package fr.raksrinana.rsndiscord.api.hermitcraft.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.converter.ISO8601ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class HermitcraftVideo{
	@JsonProperty("id")
	private String id;
	@JsonProperty("uploaded")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime uploaded;
	@JsonProperty("uploadedFriendly")
	private String uploadedFriendly;
	@JsonProperty("uploadedFriendlyMobile")
	private String uploadedFriendlyMobile;
	@JsonProperty("uploader")
	private Hermit uploader;
	@JsonProperty("title")
	private String title;
	@JsonProperty("duration")
	private long duration;
	@JsonProperty("friendlyDuration")
	private String friendlyDuration;
	@JsonProperty("likeCount")
	private long likeCount;
	@JsonProperty("viewCount")
	private long viewCount;
	@JsonProperty("commentCount")
	private long commentCount;
}
