package fr.rakambda.rsndiscord.spring.interaction.slash.impl.simkl;

import fr.rakambda.rsndiscord.spring.api.exceptions.RequestFailedException;
import fr.rakambda.rsndiscord.spring.api.simkl.SimklService;
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
public class SimklRegisterCommand implements IExecutableSlashCommandGuild{
	private final SimklService simklService;
	private final LocalizationService localizationService;
	
	@Autowired
	public SimklRegisterCommand(SimklService simklService, LocalizationService localizationService){
		this.simklService = simklService;
		this.localizationService = localizationService;
	}
	
	@Override
	@NonNull
	public String getId(){
		return "register";
	}
	
	@Override
	@NonNull
	public String getPath(){
		return "simkl/register";
	}
	
	@Override
	@NonNull
	public CompletableFuture<?> executeGuild(@NonNull SlashCommandInteraction event, @NonNull Guild guild, @NonNull Member member) throws RequestFailedException{
		var deferred = event.deferReply(true).submit();
		var locale = event.getUserLocale();
		
		if(simklService.isUserRegistered(member.getIdLong())){
			var content = localizationService.translate(locale, "simkl.already-registered");
			return deferred.thenCompose(empty -> JDAWrappers.edit(event, content).submit());
		}
		
		var deviceCode = simklService.getDeviceCode();
		var content = localizationService.translate(locale, "simkl.register-url", deviceCode.getVerificationUrl(), deviceCode.getUserCode());
		
		return deferred
				.thenCompose(empty -> JDAWrappers.edit(event, content).submit())
				.thenCompose(empty -> simklService.pollDeviceToken(event.getUser().getIdLong(), deviceCode))
				.thenCompose(empty -> JDAWrappers.edit(event, localizationService.translate(locale, "simkl.authenticated")).submit())
				.exceptionally(throwable -> handleError(throwable, event));
	}
	
	@Nullable
	private <T> T handleError(@NonNull Throwable throwable, @NonNull IReplyCallback event){
		log.error("Failed to register user", throwable);
		JDAWrappers.edit(event, localizationService.translate(event.getUserLocale(), "simkl.authentication-failed")).submit();
		return null;
	}
}
