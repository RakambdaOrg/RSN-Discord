package fr.raksrinana.rsndiscord.utils.jda.wrappers.member;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Log4j2
public class KickWrapper extends ActionWrapper<Void, AuditableRestAction<Void>>{
	private final Member member;
	private String reason;
	
	public KickWrapper(@NotNull Guild guild, @NotNull Member member){
		super(guild.kick(member));
		this.member = member;
	}
	
	@NotNull
	public KickWrapper reason(@Nullable String reason){
		getAction().reason(reason);
		this.reason = reason;
		return this;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Kicked {} with reason: {}", member, reason);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to kick user {}", member, throwable);
	}
}
