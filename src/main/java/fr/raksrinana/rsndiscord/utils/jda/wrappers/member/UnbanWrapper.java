package fr.raksrinana.rsndiscord.utils.jda.wrappers.member;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class UnbanWrapper{
	private final UserSnowflake userSnowflake;
	private final AuditableRestAction<Void> action;
	
	public UnbanWrapper(@NotNull Guild guild, @NotNull UserSnowflake userSnowflake){
		this.userSnowflake = userSnowflake;
		action = guild.unban(userSnowflake);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> log.info("Unbanned member {}", userSnowflake));
	}
}
