package fr.rakambda.rsndiscord.spring.audio.exception;

import org.jspecify.annotations.NonNull;

public class LoadFailureException extends TrackLoadException{
	public LoadFailureException(@NonNull Throwable throwable){
		super("Failed to load track", throwable);
	}
}
