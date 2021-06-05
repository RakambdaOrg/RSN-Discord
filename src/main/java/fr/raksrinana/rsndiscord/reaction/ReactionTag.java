package fr.raksrinana.rsndiscord.reaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.jetbrains.annotations.NotNull;

public enum ReactionTag{
	@Deprecated
	DELETE_CHANNEL,
	MEDIA_REACTION,
	NONE,
	@Deprecated
	SCHEDULED_DELETE_CHANNEL,
	TODO;
	
	@JsonCreator
	@NotNull
	public static ReactionTag getFromString(@NotNull String value){
		return ReactionTag.valueOf(value);
	}
}
