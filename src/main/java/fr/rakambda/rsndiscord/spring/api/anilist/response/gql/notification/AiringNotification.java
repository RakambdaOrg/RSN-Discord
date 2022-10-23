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
@JsonTypeName("AIRING")
public final class AiringNotification extends Notification{
	private int episode;
	private Media media;
	
	@Override
	public NotificationType getType(){
		return NotificationType.AIRING;
	}
}
