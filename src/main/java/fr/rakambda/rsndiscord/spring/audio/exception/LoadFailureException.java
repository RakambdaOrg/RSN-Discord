package fr.rakambda.rsndiscord.spring.audio.exception;

import org.jetbrains.annotations.NotNull;

public class LoadFailureException extends TrackLoadException{
	public LoadFailureException(@NotNull Throwable throwable){
		super("Failed to load track", throwable);
	}
}
