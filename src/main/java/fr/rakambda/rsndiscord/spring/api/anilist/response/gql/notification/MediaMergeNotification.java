package fr.rakambda.rsndiscord.spring.api.anilist.response.gql.notification;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media.Media;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeName("MEDIA_MERGE")
public final class MediaMergeNotification extends Notification{
	private Set<String> deletedMediaTitles = new HashSet<>();
	private String context;
	private String reason;
	private Media media;
	
	@Override
	public NotificationType getType(){
		return NotificationType.MEDIA_MERGE;
	}
}
