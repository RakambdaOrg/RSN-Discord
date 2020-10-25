package fr.raksrinana.rsndiscord.modules.reaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.NonNull;

public enum ReactionTag{
	NONE, TODO, ANILIST_TODO, SCHEDULED_DELETE_CHANNEL, DELETE_CHANNEL, EXTERNAL_TODO, MEDIA_REACTION;
	
	@JsonCreator
	@NonNull
	public static ReactionTag getFromString(@NonNull final String value){
		return ReactionTag.valueOf(value);
	}
}
