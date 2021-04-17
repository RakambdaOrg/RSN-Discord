package fr.raksrinana.rsndiscord.utils.jda.wrappers;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

public class KickWrapper{
	private final Guild guild;
	private final Member member;
	private final String reason;
	private final AuditableRestAction<Void> action;
	
	public KickWrapper(@NotNull Guild guild, @NotNull Member member, @Nullable String reason){
		this.guild = guild;
		this.member = member;
		this.reason = reason;
		this.action = guild.kick(member, reason);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> Log.getLogger(guild).info("Kicked {} with reason: {}", member, reason));
	}
}
