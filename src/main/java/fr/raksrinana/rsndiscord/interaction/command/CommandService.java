package fr.raksrinana.rsndiscord.interaction.command;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.interaction.command.api.ICommand;
import fr.raksrinana.rsndiscord.interaction.command.api.IExecutableCommand;
import fr.raksrinana.rsndiscord.interaction.command.api.IRegistrableCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.api.BotSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.user.api.BotUserCommand;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static fr.raksrinana.rsndiscord.utils.Utilities.getAllAnnotatedWith;

@Log4j2
public class CommandService{
	private static final Map<String, ICommand> commands;
	private static final Map<String, IExecutableCommand<?>> executableCommands;
	private static final Map<String, IRegistrableCommand> registrableCommands;
	static{
		commands = getAllCommands()
				.map(ICommand::getCommandMap)
				.map(Map::entrySet)
				.flatMap(Set::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		executableCommands = commands.entrySet().stream()
				.filter(entry -> entry.getValue() instanceof IExecutableCommand)
				.map(entry -> Map.entry(entry.getKey(), (IExecutableCommand<?>) entry.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		registrableCommands = commands.entrySet().stream()
				.filter(entry -> entry.getValue() instanceof IRegistrableCommand)
				.map(entry -> Map.entry(entry.getKey(), (IRegistrableCommand) entry.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	@NotNull
	private static Stream<ICommand> getAllCommands(){
		return Stream.concat(
				getAllAnnotatedWith(BotSlashCommand.class, clazz -> (ICommand) clazz.getConstructor().newInstance()),
				getAllAnnotatedWith(BotUserCommand.class, clazz -> (ICommand) clazz.getConstructor().newInstance())
		);
	}
	
	@NotNull
	public static CompletableFuture<Void> registerGlobalCommands(){
		var clearing = CompletableFuture.completedFuture(null); //clearGlobalCommands();
		log.info("Registering global slash commands");
		
		var commands = registrableCommands.values().stream()
				.filter(cmd -> !cmd.isGuildOnly())
				.map(IRegistrableCommand::getDefinition)
				.collect(Collectors.toSet());
		
		return clearing.thenCompose(empty -> registerCommands(Main.getJda().updateCommands(), commands));
	}
	
	@NotNull
	public static CompletableFuture<Void> registerGuildCommands(@NotNull Guild guild){
		var clearing = clearGuildCommands(guild);
		log.info("Registering guild slash commands for {}", guild);
		
		var commands = registrableCommands.values().stream()
				.filter(IRegistrableCommand::isGuildOnly)
				.map(IRegistrableCommand::getDefinition)
				.collect(Collectors.toSet());
		
		return clearing.thenCompose(empty -> registerCommands(guild.updateCommands(), commands));
	}
	
	@NotNull
	public static CompletableFuture<Void> registerCommands(@NotNull CommandListUpdateAction action, @NotNull Collection<CommandData> commands){
		if(commands.isEmpty()){
			log.info("No commands to register");
			return CompletableFuture.completedFuture(null);
		}
		
		log.info("Registering {} commands", commands.size());
		return action.addCommands(commands).submit()
				.thenAccept(slashCommands -> log.info("Slash commands registered: {}", slashCommands.stream()
						.map(Command::getName)
						.collect(Collectors.joining(", "))))
				.exceptionally(e -> {
					log.error("Failed to register slash commands", e);
					return null;
				});
	}
	
	@NotNull
	private static CompletableFuture<Void> clearGlobalCommands(){
		log.info("Clearing global slash commands");
		var jda = Main.getJda();
		return clearCommands(jda.retrieveCommands(), jda::deleteCommandById);
	}
	
	@NotNull
	public static CompletableFuture<Void> clearGuildCommands(@NotNull Guild guild){
		log.info("Clearing guild commands for guild {}", guild);
		return clearCommands(guild.retrieveCommands(), guild::deleteCommandById);
	}
	
	@NotNull
	private static CompletableFuture<Void> clearCommands(@NotNull RestAction<List<Command>> retrieve, @NotNull Function<String, RestAction<Void>> deleteCommand){
		return retrieve.submit().thenCompose(commands -> commands.stream()
				.map(command -> {
					log.info("Clearing command {}", command);
					return deleteCommand.apply(command.getId()).submit();
				})
				.reduce((left, right) -> left.thenCombine(right, (v1, v2) -> null))
				.orElseGet(() -> CompletableFuture.completedFuture(null)));
	}
	
	public static void resetAllIfDev(){
		if(Main.DEVELOPMENT){
			log.info("Development mode activated, removing commands");
			try{
				var jda = Main.getJda();
				
				clearGlobalCommands()
						.thenCompose(empty -> jda.getGuilds().stream()
								.map(CommandService::clearGuildCommands)
								.reduce((left, right) -> left.thenCombine(right, (v1, v2) -> null))
								.orElseGet(() -> CompletableFuture.completedFuture(null)))
						.get(5, TimeUnit.MINUTES);
			}
			catch(Exception e){
				log.error("Failed to reset commands", e);
			}
		}
	}
	
	@NotNull
	public static <T extends IExecutableCommand<?>> Optional<T> getExecutableCommand(@NotNull String path, Class<T> clazz){
		return Optional.ofNullable(executableCommands.get(path))
				.filter(clazz::isInstance)
				.map(clazz::cast);
	}
	
	@NotNull
	public static Optional<IRegistrableCommand> getRegistrableCommand(@NotNull String path){
		return Optional.ofNullable(registrableCommands.get(path));
	}
}
