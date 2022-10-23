package fr.rakambda.rsndiscord.spring.api.anilist.response.gql.notification;

import com.fasterxml.jackson.annotation.JsonTypeName;
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
@JsonTypeName("MEDIA_MERGE")
public final class MediaDeletionNotification extends Notification{
	private String deletedMediaTitle;
	private String context;
	private String reason;
	
	@Override
	public NotificationType getType(){
		return NotificationType.MEDIA_DELETION;
	}
}
