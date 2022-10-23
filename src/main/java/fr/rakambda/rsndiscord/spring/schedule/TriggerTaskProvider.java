package fr.rakambda.rsndiscord.spring.schedule;

import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.Trigger;

public interface TriggerTaskProvider extends Runnable, Trigger{
	@NotNull
	String getId();
}
