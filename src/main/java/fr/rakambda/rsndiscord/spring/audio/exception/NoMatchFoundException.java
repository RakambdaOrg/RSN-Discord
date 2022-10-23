package fr.rakambda.rsndiscord.spring.audio.exception;

public class NoMatchFoundException extends TrackLoadException{
	public NoMatchFoundException(){
		super("No match found");
	}
}
