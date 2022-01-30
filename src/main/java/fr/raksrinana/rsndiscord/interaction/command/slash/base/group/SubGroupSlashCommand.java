package fr.raksrinana.rsndiscord.interaction.command.slash.base.group;

import fr.raksrinana.rsndiscord.interaction.command.api.ICommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.api.ISubSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.api.ISubGroupSlashCommand;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SubGroupSlashCommand extends GroupSlashCommand implements ISubGroupSlashCommand{
	private final Map<String, ISubSlashCommand> subcommands;
	
	protected SubGroupSlashCommand(){
		this.subcommands = new HashMap<>();
	}
	
	public void addSubcommand(@NotNull ISubSlashCommand subcommand){
		subcommands.put(subcommand.getId(), subcommand);
	}
	
	@Override
	@NotNull
	public SubcommandGroupData getSlashCommand(){
		var command = new SubcommandGroupData(getId(), getShortDescription());
		
		command.addSubcommands(subcommands.values().stream()
				.map(ISubSlashCommand::getSlashCommand)
				.collect(Collectors.toList()));
		
		return command;
	}
	
	@Override
	public @NotNull Map<String, ? extends ICommand> getCommandMap(){
		return getCommandMap(subcommands.values());
	}
}
