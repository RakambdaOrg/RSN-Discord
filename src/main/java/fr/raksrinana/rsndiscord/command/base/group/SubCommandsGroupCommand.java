package fr.raksrinana.rsndiscord.command.base.group;

import fr.raksrinana.rsndiscord.command.api.ICommand;
import fr.raksrinana.rsndiscord.command.api.IGroupCommand;
import fr.raksrinana.rsndiscord.command.api.ISubCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SubCommandsGroupCommand extends GroupCommand implements IGroupCommand{
	private final Map<String, ISubCommand> subcommands;
	
	protected SubCommandsGroupCommand(){
		this.subcommands = new HashMap<>();
	}
	
	public void addSubcommand(@NotNull ISubCommand subcommand){
		subcommands.put(subcommand.getId(), subcommand);
	}
	
	@Override
	public @NotNull CommandData getSlashCommand(){
		var command = new CommandData(getId(), getShortDescription())
				.setDefaultEnabled(getDefaultPermission());
		
		command.addSubcommands(subcommands.values().stream()
				.map(ISubCommand::getSlashCommand)
				.collect(Collectors.toList()));
		
		return command;
	}
	
	@NotNull
	public Map<String, ? extends ICommand> getCommandMap(){
		return getCommandMap(subcommands.values());
	}
}
