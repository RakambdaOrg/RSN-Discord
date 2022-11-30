package fr.rakambda.rsndiscord.spring.interaction.slash;

import fr.rakambda.rsndiscord.spring.BotException;
import fr.rakambda.rsndiscord.spring.amqp.RabbitService;
import fr.rakambda.rsndiscord.spring.interaction.exception.NotAllowedException;
import fr.rakambda.rsndiscord.spring.interaction.exception.NotAvailableInGuildException;
import fr.rakambda.rsndiscord.spring.interaction.exception.NotAvailableInUserException;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommand;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandUser;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class SlashCommandRunner{
	private final SlashCommandService slashCommandService;
	private final LocalizationService localizationService;
	private final RabbitService rabbitService;
	
	@Autowired
	public SlashCommandRunner(SlashCommandService slashCommandService, LocalizationService localizationService, RabbitService rabbitService){
		this.slashCommandService = slashCommandService;
		this.localizationService = localizationService;
		this.rabbitService = rabbitService;
	}
	
	public void execute(@NotNull SlashCommandInteractionEvent event){
		CompletableFuture.completedFuture(event.getFullCommandName())
				.thenApply(fullCommandName -> slashCommandService.getExecutableCommand(fullCommandName)
						.orElseThrow(() -> new IllegalStateException("Unknown command %s".formatted(fullCommandName))))
				.thenCompose(cmd -> verifyAllowed(event, cmd))
				.thenCompose(cmd -> runCommand(event, cmd))
				.exceptionally(ex -> handleExecutionError(event, ex));
	}
	
	@NotNull
	private CompletableFuture<?> runCommand(@NotNull SlashCommandInteractionEvent event, @NotNull IExecutableSlashCommand command){
		try{
			if(event.isFromGuild()){
				
				if(!(command instanceof IExecutableSlashCommandGuild commandGuild)){
					return CompletableFuture.failedFuture(new NotAvailableInGuildException());
				}
				return commandGuild.executeGuild(event, Objects.requireNonNull(event.getGuild()), Objects.requireNonNull(event.getMember()));
			}
			else{
				if(!(command instanceof IExecutableSlashCommandUser commandUser)){
					return CompletableFuture.failedFuture(new NotAvailableInUserException());
				}
				return commandUser.executeUser(event);
			}
		}
		catch(Exception e){
			return CompletableFuture.failedFuture(e);
		}
	}
	
	@NotNull
	private CompletableFuture<IExecutableSlashCommand> verifyAllowed(@NotNull SlashCommandInteraction event, @NotNull IExecutableSlashCommand command){
		if(!command.getPermission().isAllowed(event.getUser())){
			return CompletableFuture.failedFuture(new NotAllowedException());
		}
		return CompletableFuture.completedFuture(command);
	}
	
	@Nullable
	private <T> T handleExecutionError(@NotNull SlashCommandInteractionEvent event, @NotNull Throwable ex){
		log.error("Failed to execute command {}", event.getFullCommandName(), ex);
		
		var exceptionMessage = ex instanceof BotException bex
				? localizationService.translate(event.getUserLocale(), bex.getFriendlyMessageKey(), bex.getFriendlyMessageArgs())
				: ex.getMessage();
		var message = "Failed to execute command %s: %s".formatted(event.getFullCommandName(), exceptionMessage);
		
		if(event.isAcknowledged()){
			JDAWrappers.edit(event, message).submitAndDelete(5, rabbitService);
		}
		else{
			JDAWrappers.reply(event, message).ephemeral(true).submit();
		}
		return null;
	}
}
