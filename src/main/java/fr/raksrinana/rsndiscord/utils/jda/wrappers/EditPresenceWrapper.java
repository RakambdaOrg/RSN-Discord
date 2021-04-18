package fr.raksrinana.rsndiscord.utils.jda.wrappers;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EditPresenceWrapper{
	private final Presence action;
	
	public EditPresenceWrapper(){
		this.action = Main.getJda().getPresence();
	}
	
	@NotNull
	public EditPresenceWrapper setStatus(OnlineStatus status){
		Log.getLogger().info("Set status to {}", status);
		action.setStatus(status);
		return this;
	}
	
	@NotNull
	public EditPresenceWrapper setActivity(@Nullable Activity activity){
		Log.getLogger().info("Set activity to {}", activity);
		action.setActivity(activity);
		return this;
	}
}
