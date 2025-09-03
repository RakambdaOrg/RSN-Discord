package fr.rakambda.rsndiscord.spring.schedule;

import org.jspecify.annotations.NonNull;
import org.springframework.scheduling.Trigger;

public interface TriggerTaskProvider extends Runnable, Trigger{
	@NonNull
	String getId();
}
