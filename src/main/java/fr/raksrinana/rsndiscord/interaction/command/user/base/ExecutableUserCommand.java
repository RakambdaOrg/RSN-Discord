package fr.raksrinana.rsndiscord.interaction.command.user.base;

import fr.raksrinana.rsndiscord.interaction.command.api.IExecutableCommand;
import net.dv8tion.jda.api.interactions.commands.context.UserContextInteraction;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

public abstract class ExecutableUserCommand implements IExecutableCommand<UserContextInteraction>{
	@Override
	@NotNull
	public Map<String, IExecutableCommand<UserContextInteraction>> getCommandMap(){
		return Map.of(getId(), this);
	}
}
