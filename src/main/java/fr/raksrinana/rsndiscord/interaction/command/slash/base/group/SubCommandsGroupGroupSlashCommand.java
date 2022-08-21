package fr.raksrinana.rsndiscord.interaction.command.slash.base.group;

import fr.raksrinana.rsndiscord.interaction.InteractionService;
import fr.raksrinana.rsndiscord.interaction.command.api.ICommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.api.IGroupSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.api.ISubGroupSlashCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SubCommandsGroupGroupSlashCommand extends GroupSlashCommand implements IGroupSlashCommand{
	private final Map<String, ISubGroupSlashCommand> subcommandGroups;
	
	protected SubCommandsGroupGroupSlashCommand(){
		subcommandGroups = new HashMap<>();
	}
	
	public void addSubcommandGroup(@NotNull ISubGroupSlashCommand subcommandGroup){
		subcommandGroups.put(subcommandGroup.getId(), subcommandGroup);
	}
	
	@Override
	@NotNull
	public CommandData getDefinition(){
		var command = Commands.slash(getId(), getShortDescription())
				.setLocalizationFunction(InteractionService.getLocalizationFunction())
				.setGuildOnly(isGuildOnly())
				.setDefaultPermissions(getDefaultPermission());
		
		command.addSubcommandGroups(subcommandGroups.values().stream()
				.map(ISubGroupSlashCommand::getSlashCommand)
				.collect(Collectors.toList()));
		
		return command;
	}
	
	@Override
	@NotNull
	public Map<String, ? extends ICommand> getCommandMap(){
		return getCommandMap(subcommandGroups.values());
	}
}
