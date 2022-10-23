package fr.rakambda.rsndiscord.spring.jda.wrappers.member;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class DeafenWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final Member member;
	private final boolean state;
	
	public DeafenWrapper(@NotNull Guild guild, @NotNull Member member, boolean state){
		super(guild.deafen(member, state));
		this.member = member;
		this.state = state;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Setting deaf state of {} to {}", member, state);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to deafen user {}", member, throwable);
	}
}
