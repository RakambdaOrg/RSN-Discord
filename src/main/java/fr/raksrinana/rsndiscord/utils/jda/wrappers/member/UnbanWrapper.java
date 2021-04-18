package fr.raksrinana.rsndiscord.utils.jda.wrappers.member;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public class UnbanWrapper{
	private final Guild guild;
	private final String userId;
	private final AuditableRestAction<Void> action;
	
	public UnbanWrapper(@NotNull Guild guild, @NotNull String userId){
		this.guild = guild;
		this.userId = userId;
		this.action = guild.unban(userId);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> Log.getLogger(guild).info("Unbanned member {}", userId));
	}
}
