package fr.raksrinana.rsndiscord.utils.hermitcraft.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.ISO8601DateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class HermitcraftVideo{
	@JsonProperty("id")
	private String id;
	@JsonProperty("uploaded")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime uploaded;
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
