package fr.raksrinana.rsndiscord.modules.anilist;

import lombok.NonNull;
import java.io.Serial;

class AniListException extends Exception{
	@Serial
	private static final long serialVersionUID = 2679562906118469667L;
	
	AniListException(final int status, @NonNull final String message){
		super(String.format("HTTP error %d with message \"%s\"", status, message));
	}
}
