package fr.raksrinana.rsndiscord.utils.jda.wrappers.member;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.TimeUnit;

@Log4j2
public class BanWrapper extends ActionWrapper<Void, AuditableRestAction<Void>>{
	private final Member member;
	private final int deletionDays;
	private String reason;
	
	public BanWrapper(@NotNull Guild guild, @NotNull Member member, int deletionDays){
		super(guild.ban(member, deletionDays, TimeUnit.DAYS));
		this.member = member;
		this.deletionDays = deletionDays;
	}
	
	@NotNull
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
