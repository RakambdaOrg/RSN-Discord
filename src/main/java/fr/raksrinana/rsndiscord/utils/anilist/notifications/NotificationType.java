package fr.raksrinana.rsndiscord.utils.anilist.notifications;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum NotificationType{
	ACTIVITY_LIKE,
	ACTIVITY_MENTION,
	ACTIVITY_MESSAGE,
	ACTIVITY_REPLY,
	ACTIVITY_REPLY_LIKE,
	ACTIVITY_REPLY_SUBSCRIBED,
	AIRING, FOLLOWING,
	RELATED_MEDIA_ADDITION,
	THREAD_COMMENT_LIKE,
	THREAD_COMMENT_MENTION,
	THREAD_COMMENT_REPLY,
	THREAD_LIKE,
	THREAD_SUBSCRIBED;
	
	@JsonCreator
	@NonNull
	public static NotificationType getFromString(@NonNull final String value){
		return NotificationType.valueOf(value);
	}
}
