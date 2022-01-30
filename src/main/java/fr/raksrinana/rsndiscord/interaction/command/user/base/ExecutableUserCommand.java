package fr.raksrinana.rsndiscord.interaction.command.user.base;

import fr.raksrinana.rsndiscord.interaction.command.api.IExecutableCommand;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

public abstract class ExecutableUserCommand implements IExecutableCommand{
	@Override
	@NotNull
	public Map<String, IExecutableCommand> getCommandMap(){
		return Map.of(getId(), this);
	}
}
