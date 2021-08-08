package fr.raksrinana.rsndiscord.command.base.group;

import fr.raksrinana.rsndiscord.command.api.ICommand;
import fr.raksrinana.rsndiscord.command.api.IGroupCommand;
import fr.raksrinana.rsndiscord.command.api.ISubCommandGroup;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SubCommandsGroupGroupCommand extends GroupCommand implements IGroupCommand{
	private final Map<String, ISubCommandGroup> subcommandGroups;
	
	protected SubCommandsGroupGroupCommand(){
		this.subcommandGroups = new HashMap<>();
	}
	
	public void addSubcommandGroup(@NotNull ISubCommandGroup subcommandGroup){
		subcommandGroups.put(subcommandGroup.getId(), subcommandGroup);
	}
	
	@Override
	@NotNull
	public CommandData getSlashCommand(){
		var command = new CommandData(getId(), getShortDescription())
				.setDefaultEnabled(getDefaultPermission());
		
		command.addSubcommandGroups(subcommandGroups.values().stream()
				.map(ISubCommandGroup::getSlashCommand)
				.collect(Collectors.toList()));
		
		return command;
	}
	
	@Override
	public @NotNull Map<String, ? extends ICommand> getCommandMap(){
		return getCommandMap(subcommandGroups.values());
	}
}
