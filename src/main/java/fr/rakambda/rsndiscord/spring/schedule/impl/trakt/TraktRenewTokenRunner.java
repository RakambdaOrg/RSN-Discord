package fr.rakambda.rsndiscord.spring.schedule.impl.trakt;

import fr.rakambda.rsndiscord.spring.api.trakt.TraktService;
import fr.rakambda.rsndiscord.spring.interaction.slash.impl.external.TraktRegisterCommand;
import fr.rakambda.rsndiscord.spring.interaction.slash.impl.external.TraktUnregisterCommand;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.schedule.WrappedTriggerTask;
import fr.rakambda.rsndiscord.spring.storage.entity.TraktEntity;
import fr.rakambda.rsndiscord.spring.storage.repository.TraktRepository;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

@Component
@Slf4j
public class TraktRenewTokenRunner extends WrappedTriggerTask{
	private final TraktRepository traktRepository;
	private final TraktService traktService;
	
	@Autowired
	protected TraktRenewTokenRunner(@NotNull JDA jda, TraktRepository traktRepository, TraktService traktService){
		super(jda);
		this.traktRepository = traktRepository;
		this.traktService = traktService;
	}
	
	@Override
	@NotNull
	public String getId(){
		return "trakt.token.renew";
	}
	
	@Override
	@NotNull
	protected String getName(){
		return "Trakt renew token";
	}
	
	@Override
	protected long getPeriod(){
		return 6;
	}
	
	@Override
	@NotNull
	protected TemporalUnit getPeriodUnit(){
		return ChronoUnit.HOURS;
	}
	
	@Override
	protected void executeGlobal(@NotNull JDA jda){
		traktRepository.findAllByEnabledIsTrue().forEach(this::processEntity);
	}
	
	private void processEntity(@NotNull TraktEntity entity){
		if(Objects.isNull(entity.getAccessToken())){
			return;
		}
		
		if(entity.getRefreshTokenExpire().plus(getPeriod() * 4, getPeriodUnit()).isBefore(Instant.now())){
			return;
		}
		
		try{
			traktService.renewToken(entity.getId());
		}
		catch(WebClientResponseException.BadRequest e){
			var message = "Your Trakt connection is over, you need to renew it with `%s` in one of the servers I'm in.\nIf you want to stop sync, please use `%s`."
					.formatted(
							new TraktRegisterCommand(traktService, null).getPath(),
							new TraktUnregisterCommand(traktRepository).getPath()
					);
			
			entity.setEnabled(false);
			entity.setRefreshTokenExpire(Instant.now());
			
			JDAWrappers.findUser(jda, entity.getId())
					.map(User::openPrivateChannel)
					.map(RestAction::complete)
					.map(c -> JDAWrappers.message(c, message).submit()
							.thenAccept(m -> traktRepository.save(entity)));
			log.error("Failed to renew token", e);
		}
		catch(Exception e){
			log.error("Failed to renew token", e);
		}
	}
	
	@Override
	protected void executeGuild(@NotNull Guild guild) throws Exception{
	
	}
}
