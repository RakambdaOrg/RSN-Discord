package fr.raksrinana.rsndiscord.command.base.group;

import fr.raksrinana.rsndiscord.command.api.ICommand;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

abstract class GroupCommand implements ICommand{
	@NotNull
	public Map<String, ? extends ICommand> getCommandMap(@NotNull Collection<? extends ICommand> subCommands){
		var commands = new HashMap<String, ICommand>();
		commands.put(getId(), this);
		
		subCommands.forEach(subCommand -> subCommand.getCommandMap()
				.forEach((key, value) -> commands.put("%s/%s".formatted(getId(), key), value)));
		
		return commands;
	}
}
