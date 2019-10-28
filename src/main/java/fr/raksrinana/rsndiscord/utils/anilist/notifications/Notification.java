package fr.raksrinana.rsndiscord.utils.anilist.notifications;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.anilist.AniListObject;
import fr.raksrinana.rsndiscord.utils.anilist.DatedObject;
import fr.raksrinana.rsndiscord.utils.json.SQLTimestampDeserializer;
import javax.annotation.Nonnull;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = AiringNotification.class, name = "AIRING"),
		@JsonSubTypes.Type(value = RelatedMediaNotification.class, name = "RELATED_MEDIA_ADDITION")
})
public abstract class Notification implements DatedObject{
	private final NotificationType type;
	@JsonProperty("id")
	private int id;
	@JsonProperty("createdAt")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private LocalDateTime createdAt = LocalDateTime.now();
	
	public Notification(NotificationType type){this.type = type;}
	
	@Override
	public int compareTo(@Nonnull AniListObject o){
		if(o instanceof DatedObject){
			return this.getDate().compareTo(((DatedObject) o).getDate());
		}
		return Integer.compare(this.getId(), o.getId());
	}
	
	@Nonnull
	@Override
	public LocalDateTime getDate(){
		return this.createdAt;
	}
	
	@Override
	public int getId(){
		return this.id;
	}
	
	public static String getQuery(){
		return "notifications(type_in: $type_in){\n" + "... on " + AiringNotification.getQuery() + "\n" + "... on " + RelatedMediaNotification.getQuery() + "\n" + "}\n";
	}
}
