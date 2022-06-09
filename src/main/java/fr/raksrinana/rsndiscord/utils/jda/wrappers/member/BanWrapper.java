package fr.raksrinana.rsndiscord.utils.jda.wrappers.member;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Log4j2
public class BanWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final Member member;
	private final int deletionDays;
	private final String reason;
	
	public BanWrapper(@NotNull Guild guild, @NotNull Member member, int deletionDays, @Nullable String reason){
		super(guild.ban(member, deletionDays, reason));
		this.member = member;
		this.deletionDays = deletionDays;
		this.reason = reason;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Banned {} deleting his messages for the last {} days with the reason: {}", member, deletionDays, reason);
	}
}
