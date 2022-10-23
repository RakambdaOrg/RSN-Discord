package fr.rakambda.rsndiscord.spring.api.anilist.response.gql.notification;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media.Media;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeName("MEDIA_DATA_CHANGE")
public final class MediaDataChangeNotification extends Notification{
	private String context;
	private String reason;
	private Media media;
	
	@Override
	public NotificationType getType(){
		return NotificationType.MEDIA_DATA_CHANGE;
	}
}
