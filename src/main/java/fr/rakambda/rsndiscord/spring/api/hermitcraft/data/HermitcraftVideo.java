package fr.rakambda.rsndiscord.spring.api.hermitcraft.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.rsndiscord.spring.json.converter.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HermitcraftVideo{
	private String id;
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime uploaded;
	private String uploadedFriendly;
	private String uploadedFriendlyMobile;
	private Hermit uploader;
	private String title;
	private long duration;
	private String friendlyDuration;
	private long likeCount;
	private long viewCount;
	private long commentCount;
}
