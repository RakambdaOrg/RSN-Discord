package fr.rakambda.rsndiscord.spring.jda.wrappers;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Slf4j
public class EditPresenceWrapper{
	private final Presence action;
	
	public EditPresenceWrapper(@NonNull JDA jda){
		action = jda.getPresence();
	}
	
	@NonNull
	public EditPresenceWrapper setStatus(OnlineStatus status){
		log.info("Set status to {}", status);
		action.setStatus(status);
		return this;
	}
	
	@NonNull
	public EditPresenceWrapper setActivity(@Nullable Activity activity){
		log.info("Set activity to {}", activity);
		action.setActivity(activity);
		return this;
	}
}
