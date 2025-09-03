package fr.rakambda.rsndiscord.spring.jda.wrappers.member;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.RestAction;
import org.jspecify.annotations.NonNull;

@Slf4j
public class MuteWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final Member member;
	private final boolean state;
	
	public MuteWrapper(@NonNull Guild guild, @NonNull Member member, boolean state){
		super(guild.mute(member, state));
		this.member = member;
		this.state = state;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Setting mute state of {} to {}", member, state);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to mute user {}", member, throwable);
	}
}
