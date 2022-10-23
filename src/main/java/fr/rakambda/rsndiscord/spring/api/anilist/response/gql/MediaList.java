package fr.rakambda.rsndiscord.spring.api.anilist.response.gql;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media.Media;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.medialist.MediaListStatus;
import fr.rakambda.rsndiscord.spring.json.converter.SQLTimestampDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaList{
	private MediaListStatus status = MediaListStatus.UNKNOWN;
	@JsonProperty("private")
	private boolean privateItem = false;
	private FuzzyDate startedAt = new FuzzyDate();
	private FuzzyDate completedAt = new FuzzyDate();
	private Map<String, Boolean> customLists = new HashMap<>();
	private int id;
	private Media media;
	private Integer priority;
	private Integer progress;
	private Integer progressVolumes;
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private ZonedDateTime createdAt;
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private ZonedDateTime updatedAt;
	private Integer score;
	private Integer repeat;
	private String notes;
}
