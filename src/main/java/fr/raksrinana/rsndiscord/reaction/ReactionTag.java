package fr.raksrinana.rsndiscord.reaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.jetbrains.annotations.NotNull;

public enum ReactionTag{
	ANILIST_TODO,
	DELETE_CHANNEL,
	EXTERNAL_TODO,
	MEDIA_REACTION,
	NONE,
	SCHEDULED_DELETE_CHANNEL,
	TODO;
	
	@JsonCreator
	@NotNull
	public static ReactionTag getFromString(@NotNull String value){
		return ReactionTag.valueOf(value);
	}
}
