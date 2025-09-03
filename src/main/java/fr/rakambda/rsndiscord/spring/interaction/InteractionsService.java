package fr.rakambda.rsndiscord.spring.interaction;

import fr.rakambda.rsndiscord.spring.storage.entity.CommandEntity;
import fr.rakambda.rsndiscord.spring.storage.repository.CommandRepository;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.RestAction;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Service
@Slf4j
public class InteractionsService{
	private final CommandRepository commandRepository;
	
	@Autowired
	public InteractionsService(CommandRepository commandRepository){
		this.commandRepository = commandRepository;
	}
	
	@NonNull
	public CompletableFuture<Void> clearGuildCommands(@NonNull Guild guild){
		log.info("Clearing guild commands for guild {}", guild);
		return clearCommands(guild.retrieveCommands(), guild::deleteCommandById);
	}
	
	@NonNull
	private CompletableFuture<Void> clearGlobalCommands(@NonNull JDA jda){
		log.info("Clearing global slash commands");
		return clearCommands(jda.retrieveCommands(), jda::deleteCommandById);
	}
	
	@NonNull
	public CompletableFuture<Void> removeAllCommands(@NonNull JDA jda){
		log.info("Removing All commands");
		
		return clearGlobalCommands(jda)
				.thenCompose(empty -> jda.getGuilds().stream()
						.map(this::clearGuildCommands)
						.reduce((left, right) -> left.thenCombine(right, (v1, v2) -> null))
						.orElseGet(() -> CompletableFuture.completedFuture(null)));
	}
	
	@NonNull
	private CompletableFuture<Void> clearCommands(@NonNull RestAction<List<Command>> retrieve, @NonNull Function<String, RestAction<Void>> deleteCommand){
		return retrieve.submit().thenCompose(commands -> commands.stream()
				.map(command -> {
					log.info("Clearing command {}", command);
					return deleteCommand.apply(command.getId()).submit();
				})
				.reduce((left, right) -> left.thenCombine(right, (v1, v2) -> null))
				.orElseGet(() -> CompletableFuture.completedFuture(null)));
	}
	
	public boolean isCommandActivatedOnGuild(@NonNull Guild guild, @NonNull String name){
		return commandRepository.findByGuildIdAndName(guild.getIdLong(), name).map(CommandEntity::isEnabled).orElse(false);
	}
}
