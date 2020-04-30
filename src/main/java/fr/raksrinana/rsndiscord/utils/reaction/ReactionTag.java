package fr.raksrinana.rsndiscord.utils.reaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.NonNull;

public enum ReactionTag{
	NONE, TODO, ANILIST_TODO, MEDIA_REACTION, SCHEDULED_DELETE_CHANNEL, DELETE_CHANNEL;
	
	@JsonCreator
	@NonNull
	public static ReactionTag getFromString(@NonNull final String value){
		return ReactionTag.valueOf(value);
	}
}
