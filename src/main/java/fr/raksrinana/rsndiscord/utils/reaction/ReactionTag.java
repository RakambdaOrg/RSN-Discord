package fr.raksrinana.rsndiscord.utils.reaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.NonNull;

public enum ReactionTag{
	NONE, TODO, ANILIST_TODO, ACCEPTED_QUESTION, MEDIA_REACTION;
	
	@JsonCreator
	@NonNull
	public static ReactionTag getFromString(@NonNull final String value){
		return ReactionTag.valueOf(value);
	}
}
