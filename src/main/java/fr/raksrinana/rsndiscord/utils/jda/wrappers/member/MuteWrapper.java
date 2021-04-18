package fr.raksrinana.rsndiscord.utils.jda.wrappers.member;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public class MuteWrapper{
	private final Guild guild;
	private final Member member;
	private final boolean state;
	private final AuditableRestAction<Void> action;
	
	public MuteWrapper(@NotNull Guild guild, @NotNull Member member, boolean state){
		this.guild = guild;
		this.member = member;
		this.state = state;
		this.action = guild.mute(member, state);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> Log.getLogger(guild).info("Setting mute state of {} to {}", member, state));
	}
}
