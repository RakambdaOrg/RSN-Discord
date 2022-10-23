package fr.rakambda.rsndiscord.spring.interaction.slash;

import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommand;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IRegistrableSlashCommand;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SlashCommandService{
	private final Collection<IRegistrableSlashCommand> registrableSlashCommands;
	private final Map<String, IExecutableSlashCommand> executableSlashCommands;
	
	@Autowired
	public SlashCommandService(Collection<IRegistrableSlashCommand> registrableSlashCommands, Collection<IExecutableSlashCommand> executableSlashCommands){
		this.registrableSlashCommands = registrableSlashCommands;
		this.executableSlashCommands = executableSlashCommands.stream()
				.collect(Collectors.toMap(IExecutableSlashCommand::getPath, e -> e));
	}
	
	@NotNull
	private LocalizationFunction getLocalizedFunction(){
		return ResourceBundleLocalizationFunction
				.fromBundles("lang/commands", DiscordLocale.ENGLISH_US, DiscordLocale.FRENCH)
				.build();
	}
	
	@NotNull
	public CompletableFuture<Void> registerGlobalCommands(@NotNull JDA jda){
		log.info("Registering global slash commands");
		
		var localizationFunction = getLocalizedFunction();
		var commands = registrableSlashCommands.stream()
				.map(cmd -> cmd.getDefinition(localizationFunction))
				.collect(Collectors.toSet());
		
		return registerCommands(jda.updateCommands(), commands);
	}
	
	@NotNull
	public CompletableFuture<Void> registerGuildCommands(@NotNull Guild guild){
		var clearing = clearGuildCommands(guild);
		log.info("Registering guild slash commands for {}", guild);
		
		var localizationFunction = getLocalizedFunction();
		var commands = registrableSlashCommands.stream()
				.map(cmd -> cmd.getDefinition(localizationFunction))
				.collect(Collectors.toSet());
		
		return clearing.thenCompose(empty -> registerCommands(guild.updateCommands(), commands));
	}
	
	@NotNull
	public CompletableFuture<Void> clearGuildCommands(@NotNull Guild guild){
		log.info("Clearing guild commands for guild {}", guild);
		return clearCommands(guild.retrieveCommands(), guild::deleteCommandById);
	}
	
	@NotNull
	public CompletableFuture<Void> registerCommands(@NotNull CommandListUpdateAction action, @NotNull Collection<CommandData> commands){
		if(commands.isEmpty()){
			log.info("No commands to register");
			return CompletableFuture.completedFuture(null);
		}
		
		log.info("Registering {} commands", commands.size());
		return action.addCommands(commands).submit()
				.thenAccept(registered -> log.info("Slash commands registered: {}", registered.stream()
						.map(Command::getName)
						.collect(Collectors.joining(", "))))
				.exceptionally(e -> {
					log.error("Failed to register slash commands", e);
					return null;
				});
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
	
	@NotNull
	public Optional<IExecutableSlashCommand> getExecutableCommand(@NotNull String path){
		return Optional.ofNullable(executableSlashCommands.get(path));
	}
}
