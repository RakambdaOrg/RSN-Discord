package fr.raksrinana.rsndiscord.command2;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.command2.api.ICommand;
import fr.raksrinana.rsndiscord.command2.api.IExecutableCommand;
import fr.raksrinana.rsndiscord.command2.api.IRegistrableCommand;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.Utilities.getAllAnnotatedWith;

@Log4j2
public class SlashCommandService{
	private static final Map<String, ICommand> commands;
	private static final Map<String, IExecutableCommand> executableCommands;
	private static final Map<String, IRegistrableCommand> registrableCommands;
	static{
		commands = getAllAnnotatedWith(BotSlashCommand.class, clazz -> (ICommand) clazz.getConstructor().newInstance())
				.map(ICommand::getCommandMap)
				.map(Map::entrySet)
				.flatMap(Set::stream)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		executableCommands = commands.entrySet().stream()
				.filter(entry -> entry.getValue() instanceof IExecutableCommand)
				.map(entry -> Map.entry(entry.getKey(), (IExecutableCommand) entry.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		registrableCommands = commands.entrySet().stream()
				.filter(entry -> entry.getValue() instanceof IRegistrableCommand)
				.map(entry -> Map.entry(entry.getKey(), (IRegistrableCommand) entry.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	public static void registerGlobalCommands(){
		log.info("Registering slash commands");
		var action = Main.getJda().updateCommands();
		
		var commands = registrableCommands.values().stream()
				.map(IRegistrableCommand::getSlashCommand)
				.collect(Collectors.toSet());
		
		action.addCommands(commands).submit()
				.thenAccept(slashCommands -> log.info("Global slash commands registered: {}", slashCommands.stream().map(Command::getName).collect(Collectors.joining(", "))))
				.exceptionally(e -> {
					log.error("Failed to register global slash commands", e);
					return null;
				});
	}
	
	@NotNull
	public static Optional<IExecutableCommand> getExecutableCommand(@NotNull String path){
		return Optional.ofNullable(executableCommands.get(path));
	}
	
	@NotNull
	public static Optional<IRegistrableCommand> getRegistrableCommand(@NotNull String path){
		return Optional.ofNullable(registrableCommands.get(path));
	}
	
	@NotNull
	public static CompletableFuture<Optional<Command>> getDiscordCommand(@NotNull IRegistrableCommand command, @NotNull Guild guild){
		var action = command.isGuildSpecific() ? guild.retrieveCommands() : Main.getJda().retrieveCommands();
		return action.submit().thenApply(commands -> commands.stream()
				.filter(cmd -> Objects.equals(cmd.getName(), command.getId()))
				.findFirst());
	}
	
	@NotNull
	public static CompletableFuture<List<CommandPrivilege>> updateCommandPrivileges(@NotNull IRegistrableCommand command, @NotNull Guild guild,
			@NotNull Function<List<CommandPrivilege>, Collection<? extends CommandPrivilege>> commandUpdate){
		return getDiscordCommand(command, guild)
				.thenCompose(cmd -> cmd
						.map(CompletableFuture::completedFuture)
						.orElseGet(() -> CompletableFuture.failedFuture(new IllegalStateException("Command not found")))
				)
				.thenCompose(cmd -> cmd.retrievePrivileges(guild).submit()
						.exceptionally(e -> {
							log.error("Failed to retrieve privileges", e);
							return new ArrayList<>();
						})
						.thenApply(commandUpdate)
						.thenCompose(commandPrivileges -> cmd.updatePrivileges(guild, commandPrivileges).submit())
				);
	}
}
