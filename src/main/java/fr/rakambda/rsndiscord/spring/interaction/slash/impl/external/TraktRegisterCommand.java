package fr.rakambda.rsndiscord.spring.interaction.slash.impl.external;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class TraktRegisterCommand implements IExecutableSlashCommandGuild{
	private final TraktService traktService;
	private final LocalizationService localizationService;
	
	@Autowired
	public TraktRegisterCommand(TraktService traktService, LocalizationService localizationService){
		this.traktService = traktService;
		this.localizationService = localizationService;
	}
	
	@Override
	@NotNull
	public String getId(){
		return "trakt";
	}
	
	@Override
	@NotNull
	public String getPath(){
		return "external/trakt";
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member) throws RequestFailedException{
		var deferred = event.deferReply(true).submit();
		var locale = event.getUserLocale();
		
		if(traktService.isUserRegistered(member.getIdLong())){
			var content = localizationService.translate(locale, "trakt.already-registered");
			return deferred.thenCompose(empty -> JDAWrappers.edit(event, content).submit());
		}
		
		var deviceCode = traktService.getDeviceCode();
		var content = localizationService.translate(locale, "trakt.register-url", deviceCode.getVerificationUrl(), deviceCode.getUserCode());
		
		return deferred
				.thenCompose(empty -> JDAWrappers.edit(event, content).submit())
				.thenCompose(empty -> traktService.pollDeviceToken(event.getUser().getIdLong(), deviceCode))
				.thenCompose(empty -> JDAWrappers.edit(event, localizationService.translate(locale, "trakt.authenticated")).submit())
				.exceptionally(throwable -> handleError(throwable, event));
	}
	
	@Nullable
	private <T> T handleError(@NotNull Throwable throwable, @NotNull IReplyCallback event){
		log.error("Failed to register user", throwable);
		JDAWrappers.edit(event, localizationService.translate(event.getUserLocale(), "trakt.authentication-failed")).submit();
		return null;
	}
}
