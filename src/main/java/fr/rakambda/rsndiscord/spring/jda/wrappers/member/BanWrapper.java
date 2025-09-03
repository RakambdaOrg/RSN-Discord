package fr.rakambda.rsndiscord.spring.jda.wrappers.member;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BanWrapper extends ActionWrapper<Void, AuditableRestAction<Void>>{
	private final Member member;
	private final int deletionDays;
	private String reason;
	
	public BanWrapper(@NonNull Guild guild, @NonNull Member member, int deletionDays){
		super(guild.ban(member, deletionDays, TimeUnit.DAYS));
		this.member = member;
		this.deletionDays = deletionDays;
	}
	
	@NonNull
	public BanWrapper reason(@Nullable String reason){
		getAction().reason(reason);
		this.reason = reason;
		return this;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Banned {} deleting his messages for the last {} days with the reason: {}", member, deletionDays, reason);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to ban user {}", member, throwable);
	}
}
