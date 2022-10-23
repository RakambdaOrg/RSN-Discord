package fr.rakambda.rsndiscord.spring.audio.exception;

public class AlreadyInQueueException extends SchedulerException{
	public AlreadyInQueueException(){
		super("Track already in queue");
	}
}
