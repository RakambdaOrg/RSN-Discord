package fr.raksrinana.rsndiscord.interaction.command.slash.base.group;

import fr.raksrinana.rsndiscord.interaction.command.api.ICommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.api.IGroupSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.api.ISubSlashCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SubCommandsGroupSlashCommand extends GroupSlashCommand implements IGroupSlashCommand{
	private final Map<String, ISubSlashCommand> subcommands;
	
	protected SubCommandsGroupSlashCommand(){
		subcommands = new HashMap<>();
	}
	
	public void addSubcommand(@NotNull ISubSlashCommand subcommand){
		subcommands.put(subcommand.getId(), subcommand);
	}
	
	@Override
	public @NotNull CommandData getDefinition(){
		var command = Commands.slash(getId(), getShortDescription())
				.setDefaultEnabled(getDefaultPermission());
		
		command.addSubcommands(subcommands.values().stream()
				.map(ISubSlashCommand::getSlashCommand)
				.collect(Collectors.toList()));
		
		return command;
	}
	
	@NotNull
	public Map<String, ? extends ICommand> getCommandMap(){
		return getCommandMap(subcommands.values());
	}
}
