package fr.rakambda.rsndiscord.spring.interaction.slash;

import fr.rakambda.rsndiscord.spring.interaction.exception.NotAvailableInGuildException;
import fr.rakambda.rsndiscord.spring.interaction.exception.NotAvailableInUserException;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommand;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandUser;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class SlashCommandCompletionRunner{
	private final SlashCommandService slashCommandService;
	
	@Autowired
	public SlashCommandCompletionRunner(SlashCommandService slashCommandService){
		this.slashCommandService = slashCommandService;
	}
	
	public void complete(@NonNull CommandAutoCompleteInteractionEvent event){
		CompletableFuture.completedFuture(event.getFullCommandName())
				.thenApply(path -> slashCommandService.getExecutableCommand(event.getFullCommandName())
						.orElseThrow(() -> new IllegalStateException("Unknown command %s".formatted(path))))
				.thenCompose(cmd -> runCompletion(event, cmd))
				.exceptionally(ex -> handleExecutionError(event, ex));
	}
	
	@NonNull
	private CompletableFuture<?> runCompletion(@NonNull CommandAutoCompleteInteractionEvent event, @NonNull IExecutableSlashCommand command){
		try{
			if(event.isFromGuild()){
				if(!(command instanceof IExecutableSlashCommandGuild commandGuild)){
					return CompletableFuture.failedFuture(new NotAvailableInGuildException());
				}
				return commandGuild.autoCompleteGuild(event, Objects.requireNonNull(event.getGuild()), Objects.requireNonNull(event.getMember()));
			}
			else{
				if(!(command instanceof IExecutableSlashCommandUser commandUser)){
					return CompletableFuture.failedFuture(new NotAvailableInUserException());
				}
				return commandUser.autoCompleteUser(event);
			}
		}
		catch(Exception e){
			return CompletableFuture.failedFuture(e);
		}
	}
	
	@Nullable
	private <T> T handleExecutionError(@NonNull CommandAutoCompleteInteractionEvent event, @NonNull Throwable ex){
		log.error("Failed to auto complete command {}", event.getFullCommandName(), ex);
		return null;
	}
}
