package fr.raksrinana.rsndiscord.utils.jda.wrappers.member;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Log4j2
public class KickWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final Member member;
	private final String reason;
	
	public KickWrapper(@NotNull Guild guild, @NotNull Member member, @Nullable String reason){
		super(guild.kick(member, reason));
		this.member = member;
		this.reason = reason;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Kicked {} with reason: {}", member, reason);
	}
}
