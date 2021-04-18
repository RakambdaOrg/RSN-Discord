package fr.raksrinana.rsndiscord.utils.jda.wrappers.guild;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public class LeaveGuildWrapper{
	private final Guild guild;
	private final RestAction<Void> action;
	
	public LeaveGuildWrapper(@NotNull Guild guild){
		this.guild = guild;
		this.action = guild.leave();
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> Log.getLogger(guild).info("Left guild {}", guild));
	}
}
