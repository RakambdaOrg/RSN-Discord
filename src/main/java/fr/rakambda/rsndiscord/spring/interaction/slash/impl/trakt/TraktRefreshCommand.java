package fr.rakambda.rsndiscord.spring.interaction.slash.impl.trakt;

import fr.rakambda.rsndiscord.spring.api.exceptions.RequestFailedException;
import fr.rakambda.rsndiscord.spring.api.trakt.TraktService;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class TraktRefreshCommand implements IExecutableSlashCommandGuild{
	private final TraktService traktService;
	private final LocalizationService localizationService;
	
	@Autowired
	public TraktRefreshCommand(TraktService traktService, LocalizationService localizationService){
		this.traktService = traktService;
		this.localizationService = localizationService;
	}
	
	@Override
	@NonNull
	public String getId(){
		return "refresh";
	}
	
	@Override
	@NonNull
	public String getPath(){
		return "trakt/refresh";
	}
	
	@Override
	@NonNull
	public CompletableFuture<?> executeGuild(@NonNull SlashCommandInteraction event, @NonNull Guild guild, @NonNull Member member) throws RequestFailedException{
		var deferred = event.deferReply(true).submit();
		var locale = event.getUserLocale();
		
		if(!traktService.isUserRegistered(member.getIdLong())){
			var content = localizationService.translate(locale, "trakt.not-registered");
			return deferred.thenCompose(empty -> JDAWrappers.edit(event, content).submit());
		}
		
		var renewed = false;
		try{
			traktService.renewToken(member.getIdLong());
			renewed = true;
		}
		catch(Exception e){
			log.error("Failed to renew token for {}", member.getIdLong(), e);
		}
		
		var content = localizationService.translate(locale, renewed ? "trakt.renewed-token" : "trakt.renewed-token-failed");
		return deferred
				.thenCompose(empty -> JDAWrappers.edit(event, content).submit())
				.exceptionally(throwable -> handleError(throwable, event));
	}
	
	@Nullable
	private <T> T handleError(@NonNull Throwable throwable, @NonNull IReplyCallback event){
		log.error("Failed to renew token", throwable);
		JDAWrappers.edit(event, localizationService.translate(event.getUserLocale(), "trakt.renewed-token-failed")).submit();
		return null;
	}
}
