package fr.raksrinana.rsndiscord.utils.jda.wrappers.member;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class UnbanWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final UserSnowflake userSnowflake;
	
	public UnbanWrapper(@NotNull Guild guild, @NotNull UserSnowflake userSnowflake){
		super(guild.unban(userSnowflake));
		this.userSnowflake = userSnowflake;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Unbanned member {}", userSnowflake);
	}
}
