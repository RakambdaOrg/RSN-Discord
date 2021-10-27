package fr.raksrinana.rsndiscord.utils.jda.wrappers.guild;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class LeaveGuildWrapper{
	private final Guild guild;
	private final RestAction<Void> action;
	
	public LeaveGuildWrapper(@NotNull Guild guild){
		this.guild = guild;
		action = guild.leave();
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> log.info("Left guild {}", guild));
	}
}
