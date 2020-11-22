package fr.raksrinana.rsndiscord.modules.reaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.NonNull;

public enum ReactionTag{
	ANILIST_TODO,
	DELETE_CHANNEL,
	EXTERNAL_TODO,
	MEDIA_REACTION,
	NONE,
	SCHEDULED_DELETE_CHANNEL,
	TODO;
	
	@JsonCreator
	@NonNull
	public static ReactionTag getFromString(@NonNull final String value){
		return ReactionTag.valueOf(value);
	}
}
