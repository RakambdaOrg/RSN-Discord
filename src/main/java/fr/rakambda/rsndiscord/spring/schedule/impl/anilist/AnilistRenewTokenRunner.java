package fr.rakambda.rsndiscord.spring.schedule.impl.anilist;

import fr.rakambda.rsndiscord.spring.api.anilist.AnilistService;
import fr.rakambda.rsndiscord.spring.interaction.slash.impl.anilist.AnilistRegisterCommand;
import fr.rakambda.rsndiscord.spring.interaction.slash.impl.anilist.AnilistUnregisterCommand;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.schedule.WrappedTriggerTask;
import fr.rakambda.rsndiscord.spring.storage.entity.AnilistEntity;
import fr.rakambda.rsndiscord.spring.storage.repository.AnilistRepository;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

@Component
@Slf4j
public class AnilistRenewTokenRunner extends WrappedTriggerTask{
	private final AnilistRepository anilistRepository;
	private final AnilistService anilistService;
	
	@Autowired
	protected AnilistRenewTokenRunner(@NonNull JDA jda, AnilistRepository anilistRepository, AnilistService anilistService){
		super(jda);
		this.anilistRepository = anilistRepository;
		this.anilistService = anilistService;
	}
	
	@Override
	@NonNull
	public String getId(){
		return "anilist.token.renew";
	}
	
	@Override
	@NonNull
	protected String getName(){
		return "Anilist renew token";
	}
	
	@Override
	protected long getPeriod(){
		return 6;
	}
	
	@Override
	@NonNull
	protected TemporalUnit getPeriodUnit(){
		return ChronoUnit.HOURS;
	}
	
	@Override
	protected void executeGlobal(@NonNull JDA jda){
		anilistRepository.findAllByEnabledIsTrue().forEach(this::processEntity);
	}
	
	private void processEntity(@NonNull AnilistEntity entity){
		if(Objects.nonNull(entity.getRefreshTokenExpire()) && Instant.now().isBefore(entity.getRefreshTokenExpire().minus(getPeriod() * 4, getPeriodUnit()))){
			return;
		}
		
		try{
			renewToken(entity);
		}
		catch(Exception e){
			log.error("Failed to renew token", e);
		}
	}
	
	@Override
	protected void executeGuild(@NonNull Guild guild) throws Exception{
	}
	
	public void renewToken(@NonNull AnilistEntity entity){
		var userId = entity.getId();
		log.info("Renewing token for {}", userId);
		
		var message = "Your Anilist connection is over, you need to renew it with `%s` in one of the servers I'm in.\nIf you want to stop sync, please use `%s`."
				.formatted(
						new AnilistRegisterCommand(anilistService).getPath(),
						new AnilistUnregisterCommand(anilistRepository).getPath()
				);
		
		try{
			entity.setEnabled(false);
			
			JDAWrappers.findUser(jda, userId)
					.map(User::openPrivateChannel)
					.map(RestAction::complete)
					.map(c -> JDAWrappers.message(c, message).submit()
							.thenAccept(m -> anilistRepository.save(entity)));
		}
		catch(Exception e){
			log.error("Failed to get user with id {}", userId, e);
		}
	}
}
