package fr.raksrinana.rsndiscord.utils.anilist;

import javax.annotation.Nonnull;

class AnilistException extends Exception{
	private static final long serialVersionUID = 2679562906118469667L;
	
	AnilistException(final int status, @Nonnull final String message){
		super(String.format("HTTP error %d with message \"%s\"", status, message));
	}
}
