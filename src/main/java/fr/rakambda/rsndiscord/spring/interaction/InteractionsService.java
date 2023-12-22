package fr.rakambda.rsndiscord.spring.interaction;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Service
@Slf4j
public class InteractionsService{
	@NotNull
	public CompletableFuture<Void> clearGuildCommands(@NotNull Guild guild){
		log.info("Clearing guild commands for guild {}", guild);
		return clearCommands(guild.retrieveCommands(), guild::deleteCommandById);
	}
	
	@NotNull
	private CompletableFuture<Void> clearGlobalCommands(@NotNull JDA jda){
		log.info("Clearing global slash commands");
		return clearCommands(jda.retrieveCommands(), jda::deleteCommandById);
	}
	
	@NotNull
	public CompletableFuture<Void> removeAllCommands(@NotNull JDA jda){
		log.info("Removing All commands");
		
		return clearGlobalCommands(jda)
				.thenCompose(empty -> jda.getGuilds().stream()
						.map(this::clearGuildCommands)
						.reduce((left, right) -> left.thenCombine(right, (v1, v2) -> null))
						.orElseGet(() -> CompletableFuture.completedFuture(null)));
	}
	
	@NotNull
	private CompletableFuture<Void> clearCommands(@NotNull RestAction<List<Command>> retrieve, @NotNull Function<String, RestAction<Void>> deleteCommand){
		return retrieve.submit().thenCompose(commands -> commands.stream()
				.map(command -> {
					log.info("Clearing command {}", command);
					return deleteCommand.apply(command.getId()).submit();
				})
				.reduce((left, right) -> left.thenCombine(right, (v1, v2) -> null))
				.orElseGet(() -> CompletableFuture.completedFuture(null)));
	}
}
