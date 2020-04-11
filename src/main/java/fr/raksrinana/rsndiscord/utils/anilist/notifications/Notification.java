package fr.raksrinana.rsndiscord.utils.anilist.notifications;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.anilist.AniListObject;
import fr.raksrinana.rsndiscord.utils.anilist.AnilistDatedObject;
import fr.raksrinana.rsndiscord.utils.json.SQLTimestampDeserializer;
import lombok.Getter;
import lombok.NonNull;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = AiringNotification.class, name = "AIRING"),
		@JsonSubTypes.Type(value = RelatedMediaNotification.class, name = "RELATED_MEDIA_ADDITION")
})
@Getter
public abstract class Notification implements AnilistDatedObject{
	private final NotificationType type;
	@JsonProperty("id")
	private int id;
	@JsonProperty("createdAt")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private final ZonedDateTime createdAt = ZonedDateTime.now();
	
	public Notification(NotificationType type){this.type = type;}
	
	@Override
	public int compareTo(@NonNull AniListObject o){
		if(o instanceof AnilistDatedObject){
			return this.getDate().compareTo(((AnilistDatedObject) o).getDate());
		}
		return Integer.compare(this.getId(), o.getId());
	}
	
	@NonNull
	@Override
	public ZonedDateTime getDate(){
		return this.getCreatedAt();
	}
	
	public static String getQuery(){
		return "notifications(type_in: $type_in){\n" + "... on " + AiringNotification.getQUERY() + "\n" + "... on " + RelatedMediaNotification.getQUERY() + "\n" + "}\n";
	}
}
