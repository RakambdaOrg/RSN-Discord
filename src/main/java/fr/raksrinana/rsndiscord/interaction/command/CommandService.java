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
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
	
	public static void registerGlobalCommands(){
		log.info("Registering global slash commands");
		var action = Main.getJda().updateCommands();
		
		var commands = registrableCommands.values().stream()
				.filter(cmd -> !cmd.isGuildOnly())
				.map(IRegistrableCommand::getDefinition)
				.collect(Collectors.toSet());
		
		action.addCommands(commands).submit()
				.thenAccept(slashCommands -> log.info("Global slash commands registered: {}", slashCommands.stream().map(Command::getName).collect(Collectors.joining(", "))))
				.exceptionally(e -> {
					log.error("Failed to register global slash commands", e);
					return null;
				});
	}
	
	public static void registerGuildCommands(@NotNull Guild guild){
		log.info("Registering guild slash commands for {}", guild);
		var action = guild.updateCommands();
		
		var commands = registrableCommands.values().stream()
				.map(IRegistrableCommand::getDefinition)
				.collect(Collectors.toList());
		
		action.addCommands(commands).submit()
				.thenAccept(slashCommands -> log.info("{} slash commands registered: {}", guild, slashCommands.stream()
						.map(Command::getName)
						.collect(Collectors.joining(", "))))
				.exceptionally(e -> {
					log.error("Failed to register global slash commands", e);
					return null;
				});
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
