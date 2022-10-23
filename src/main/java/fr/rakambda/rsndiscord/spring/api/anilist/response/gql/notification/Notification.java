package fr.rakambda.rsndiscord.spring.api.anilist.response.gql.notification;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.rsndiscord.spring.json.converter.SQLTimestampDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = AiringNotification.class, name = "AIRING"),
		@JsonSubTypes.Type(value = RelatedMediaAdditionNotification.class, name = "RELATED_MEDIA_ADDITION"),
		@JsonSubTypes.Type(value = MediaDataChangeNotification.class, name = "MEDIA_DATA_CHANGE"),
		@JsonSubTypes.Type(value = MediaMergeNotification.class, name = "MEDIA_MERGE"),
		@JsonSubTypes.Type(value = MediaDeletionNotification.class, name = "MEDIA_DELETION")
})
public sealed abstract class Notification permits AiringNotification, MediaDataChangeNotification, MediaDeletionNotification, MediaMergeNotification, RelatedMediaAdditionNotification{
	private int id;
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private ZonedDateTime createdAt = ZonedDateTime.now();
	
	public abstract NotificationType getType();
}
