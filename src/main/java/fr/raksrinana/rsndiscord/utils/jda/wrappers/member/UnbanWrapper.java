package fr.raksrinana.rsndiscord.utils.jda.wrappers.member;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class UnbanWrapper{
	private final String userId;
	private final AuditableRestAction<Void> action;
	
	public UnbanWrapper(@NotNull Guild guild, @NotNull String userId){
		this.userId = userId;
		action = guild.unban(userId);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> log.info("Unbanned member {}", userId));
	}
}
