package fr.raksrinana.rsndiscord.utils.jda.wrappers.member;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class KickWrapper{
	private final Member member;
	private final String reason;
	private final AuditableRestAction<Void> action;
	
	public KickWrapper(@NotNull Guild guild, @NotNull Member member, @Nullable String reason){
		this.member = member;
		this.reason = reason;
		action = guild.kick(member, reason);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> log.info("Kicked {} with reason: {}", member, reason));
	}
}
