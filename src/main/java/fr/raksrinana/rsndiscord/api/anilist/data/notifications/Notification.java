package fr.raksrinana.rsndiscord.api.anilist.data.notifications;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.api.anilist.data.IAniListDatedObject;
import fr.raksrinana.rsndiscord.api.anilist.data.IAniListObject;
import fr.raksrinana.rsndiscord.utils.json.converter.SQLTimestampDeserializer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = AiringNotification.class, name = "AIRING"),
		@JsonSubTypes.Type(value = RelatedMediaAdditionNotification.class, name = "RELATED_MEDIA_ADDITION"),
		@JsonSubTypes.Type(value = MediaDataChangeNotification.class, name = "MEDIA_DATA_CHANGE"),
		@JsonSubTypes.Type(value = MediaMergeNotification.class, name = "MEDIA_MERGE"),
		@JsonSubTypes.Type(value = MediaDeletionNotification.class, name = "MEDIA_DELETION")
})
@Getter
public abstract class Notification implements IAniListDatedObject{
	private final NotificationType type;
	@JsonProperty("createdAt")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private ZonedDateTime createdAt = ZonedDateTime.now();
	@JsonProperty("id")
	private int id;
	
	public Notification(NotificationType type){
		this.type = type;
	}
	
	@Override
	public int compareTo(@NotNull IAniListObject o){
		if(o instanceof IAniListDatedObject){
			return getDate().compareTo(((IAniListDatedObject) o).getDate());
		}
		return Integer.compare(getId(), o.getId());
	}
	
	@NotNull
	@Override
	public ZonedDateTime getDate(){
		return getCreatedAt();
	}
	
	public static String getQuery(){
		return """
				notifications(type_in: $type_in){
					... on %s
					... on %s
					... on %s
					... on %s
					... on %s
				}"""
				.formatted(
						AiringNotification.QUERY,
						RelatedMediaAdditionNotification.QUERY,
						MediaDataChangeNotification.QUERY,
						MediaMergeNotification.QUERY,
						MediaDeletionNotification.QUERY
				);
	}
}
