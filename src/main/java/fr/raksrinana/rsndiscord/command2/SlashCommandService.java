package fr.raksrinana.rsndiscord.command2;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.command2.api.ICommand;
import fr.raksrinana.rsndiscord.command2.api.IExecutableCommand;
import fr.raksrinana.rsndiscord.command2.api.IRegistrableCommand;
import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.interactions.commands.Command;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.Utilities.getAllAnnotatedWith;

public class SlashCommandService{
	private static final Map<String, ICommand> commands;
	private static final Map<String, IExecutableCommand> executableCommands;
	private static final Map<String, IRegistrableCommand> registrableCommands;
	
	public static void registerGlobalCommands(){
		var action = Main.getJda().updateCommands();
		
		var commands = registrableCommands.values().stream()
				.map(IRegistrableCommand::getSlashCommand)
				.collect(Collectors.toSet());
		
		action.addCommands(commands).submit()
				.thenAccept(slashCommands -> Log.getLogger().info("Global slash commands registered: {}", slashCommands.stream().map(Command::getName).collect(Collectors.joining(", "))))
				.exceptionally(e -> {
					Log.getLogger().error("Failed to register global slash commands", e);
					return null;
				});
	}
	
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
	public static Optional<IExecutableCommand> getExecutableCommand(String path){
		return Optional.ofNullable(executableCommands.get(path));
	}
	
	public static Optional<IRegistrableCommand> getRegistrableCommand(String path){
		return Optional.ofNullable(registrableCommands.get(path));
	}
}
