package fr.raksrinana.rsndiscord.utils.jda.wrappers.member;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class MuteWrapper{
	private final Member member;
	private final boolean state;
	private final AuditableRestAction<Void> action;
	
	public MuteWrapper(@NotNull Guild guild, @NotNull Member member, boolean state){
		this.member = member;
		this.state = state;
		action = guild.mute(member, state);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> log.info("Setting mute state of {} to {}", member, state));
	}
}
