package fr.raksrinana.rsndiscord.reaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.jetbrains.annotations.NotNull;

public enum ReactionTag{
	TODO;
	
	@JsonCreator
	@NotNull
	public static ReactionTag getFromString(@NotNull String value){
		return ReactionTag.valueOf(value);
	}
}
