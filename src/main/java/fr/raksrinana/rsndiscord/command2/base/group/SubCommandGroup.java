package fr.raksrinana.rsndiscord.command2.base.group;

import fr.raksrinana.rsndiscord.command2.api.ICommand;
import fr.raksrinana.rsndiscord.command2.api.ISubCommand;
import fr.raksrinana.rsndiscord.command2.api.ISubCommandGroup;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SubCommandGroup extends GroupCommand implements ISubCommandGroup{
	private final Map<String, ISubCommand> subcommands;
	
	protected SubCommandGroup(){
		this.subcommands = new HashMap<>();
	}
	
	public void addSubcommand(@NotNull ISubCommand subcommand){
		subcommands.put(subcommand.getId(), subcommand);
	}
	
	@Override
	@NotNull
	public SubcommandGroupData getSlashCommand(){
		var command = new SubcommandGroupData(getId(), getShortDescription());
		
		command.addSubcommands(subcommands.values().stream()
				.map(ISubCommand::getSlashCommand)
				.collect(Collectors.toList()));
		
		return command;
	}
	
	@Override
	public @NotNull Map<String, ? extends ICommand> getCommandMap(){
		return getCommandMap(subcommands.values());
	}
}
