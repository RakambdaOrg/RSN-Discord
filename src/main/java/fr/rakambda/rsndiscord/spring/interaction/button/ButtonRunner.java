package fr.rakambda.rsndiscord.spring.interaction.button;

import fr.rakambda.rsndiscord.spring.BotException;
import fr.rakambda.rsndiscord.spring.amqp.RabbitService;
import fr.rakambda.rsndiscord.spring.interaction.button.api.IExecutableButton;
import fr.rakambda.rsndiscord.spring.interaction.button.api.IExecutableButtonGuild;
import fr.rakambda.rsndiscord.spring.interaction.button.api.IExecutableButtonUser;
import fr.rakambda.rsndiscord.spring.interaction.exception.NotAvailableInGuildException;
import fr.rakambda.rsndiscord.spring.interaction.exception.NotAvailableInUserException;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ButtonRunner{
	private final ButtonService buttonService;
	private final LocalizationService localizationService;
	private final RabbitService rabbitService;
	
	@Autowired
	public ButtonRunner(ButtonService buttonService, LocalizationService localizationService, RabbitService rabbitService){
		this.buttonService = buttonService;
		this.localizationService = localizationService;
		this.rabbitService = rabbitService;
	}
	
	public void execute(@NonNull ButtonInteraction event){
		CompletableFuture.completedFuture(event.getComponentId())
				.thenApply(id -> buttonService.getExecutableButton(id)
						.orElseThrow(() -> new IllegalStateException("Unknown button %s".formatted(id))))
				.thenCompose(cmd -> runButton(event, cmd))
				.exceptionally(ex -> handleExecutionError(event, ex));
	}
	
	@NonNull
	private CompletableFuture<?> runButton(@NonNull ButtonInteraction event, @NonNull IExecutableButton modal){
		try{
			if(event.isFromGuild()){
				if(!(modal instanceof IExecutableButtonGuild modalGuild)){
					return CompletableFuture.failedFuture(new NotAvailableInGuildException());
				}
				return modalGuild.executeGuild(event, Objects.requireNonNull(event.getGuild()), Objects.requireNonNull(event.getMember()));
			}
			else{
				if(!(modal instanceof IExecutableButtonUser modalUser)){
					return CompletableFuture.failedFuture(new NotAvailableInUserException());
				}
				return modalUser.executeUser(event);
			}
		}
		catch(BotException e){
			return CompletableFuture.failedFuture(e);
		}
	}
	
	@Nullable
	private <T> T handleExecutionError(@NonNull ButtonInteraction event, @NonNull Throwable ex){
		log.error("Failed to execute button {}", event.getComponentId(), ex);
		
		var exceptionMessage = ex instanceof BotException bex
				? localizationService.translate(event.getUserLocale(), bex.getFriendlyMessageKey(), bex.getFriendlyMessageArgs())
				: ex.getMessage();
		var message = "Failed to execute button %s: %s".formatted(event.getComponentId(), exceptionMessage);
		
		if(event.isAcknowledged()){
			JDAWrappers.edit(event, message).submitAndDelete(5, rabbitService);
		}
		else{
			JDAWrappers.reply(event, message).ephemeral(true).submit();
		}
		return null;
	}
}
