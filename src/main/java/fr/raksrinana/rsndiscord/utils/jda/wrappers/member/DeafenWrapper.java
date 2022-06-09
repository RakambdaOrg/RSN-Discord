package fr.raksrinana.rsndiscord.utils.jda.wrappers.member;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

@Log4j2
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
}
