package fr.raksrinana.rsndiscord.api.anilist;

import org.jetbrains.annotations.NotNull;
import java.io.Serial;

class AniListException extends Exception{
	@Serial
	private static final long serialVersionUID = 2679562906118469667L;
	
	AniListException(int status, @NotNull String message){
		super(String.format("HTTP error %d with message \"%s\"", status, message));
	}
}
