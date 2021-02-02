package fr.raksrinana.rsndiscord.api.anilist.data.notifications;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.NotNull;

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
	@NotNull
	public static NotificationType getFromName(@NotNull String value){
		return NotificationType.valueOf(value);
	}
}
