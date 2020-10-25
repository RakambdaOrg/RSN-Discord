package fr.raksrinana.rsndiscord.modules.anilist.data.notifications;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.modules.anilist.data.IAniListObject;
import fr.raksrinana.rsndiscord.modules.anilist.data.IAnilistDatedObject;
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
public abstract class INotification implements IAnilistDatedObject{
	private final NotificationType type;
	@JsonProperty("createdAt")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private final ZonedDateTime createdAt = ZonedDateTime.now();
	@JsonProperty("id")
	private int id;
	
	public INotification(NotificationType type){this.type = type;}
	
	@Override
	public int compareTo(@NonNull IAniListObject o){
		if(o instanceof IAnilistDatedObject){
			return this.getDate().compareTo(((IAnilistDatedObject) o).getDate());
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
