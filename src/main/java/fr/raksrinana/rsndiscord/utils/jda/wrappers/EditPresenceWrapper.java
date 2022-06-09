package fr.raksrinana.rsndiscord.utils.jda.wrappers;

import fr.raksrinana.rsndiscord.Main;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Log4j2
public class EditPresenceWrapper{
	private final Presence action;
	
	public EditPresenceWrapper(){
		action = Main.getJda().getPresence();
	}
	
	@NotNull
	public EditPresenceWrapper setStatus(OnlineStatus status){
		log.info("Set status to {}", status);
		action.setStatus(status);
		return this;
	}
	
	@NotNull
	public EditPresenceWrapper setActivity(@Nullable Activity activity){
		log.info("Set activity to {}", activity);
		action.setActivity(activity);
		return this;
	}
}
