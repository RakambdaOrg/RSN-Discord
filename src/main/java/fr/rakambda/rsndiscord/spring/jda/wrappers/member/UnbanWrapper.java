package fr.rakambda.rsndiscord.spring.jda.wrappers.member;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.requests.RestAction;
import org.jspecify.annotations.NonNull;

@Slf4j
public class UnbanWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final UserSnowflake user;
	
	public UnbanWrapper(@NonNull Guild guild, @NonNull UserSnowflake user){
		super(guild.unban(user));
		this.user = user;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Unbanned member {}", user);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to unban user {}", user, throwable);
	}
}
