package fr.raksrinana.rsndiscord.utils.anilist.notifications;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.annotation.Nonnull;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum NotificationType{
	ACTIVITY_MESSAGE, ACTIVITY_REPLY, FOLLOWING, ACTIVITY_MENTION, THREAD_COMMENT_MENTION, THREAD_SUBSCRIBED, THREAD_COMMENT_REPLY, AIRING, ACTIVITY_LIKE, ACTIVITY_REPLY_LIKE, THREAD_LIKE, THREAD_COMMENT_LIKE, ACTIVITY_REPLY_SUBSCRIBED, RELATED_MEDIA_ADDITION;
	
	@JsonCreator
	@Nonnull
	public static NotificationType getFromString(@Nonnull final String value){
		return NotificationType.valueOf(value);
	}
}
