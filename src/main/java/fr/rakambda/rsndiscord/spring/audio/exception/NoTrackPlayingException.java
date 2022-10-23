package fr.rakambda.rsndiscord.spring.audio.exception;

public class NoTrackPlayingException extends SchedulerException{
	public NoTrackPlayingException(){
		super("No track currently playing");
	}
}
