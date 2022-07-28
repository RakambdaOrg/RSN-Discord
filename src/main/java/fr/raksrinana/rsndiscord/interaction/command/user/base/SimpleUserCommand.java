package fr.raksrinana.rsndiscord.interaction.command.user.base;

import fr.raksrinana.rsndiscord.interaction.command.user.api.ISimpleUserCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleUserCommand extends ExecutableUserCommand implements ISimpleUserCommand{
	@Override
	@NotNull
	public CommandData getDefinition(){
		return Commands.user(getId())
				.setGuildOnly(isGuildOnly())
				.setDefaultPermissions(getDefaultPermission());
	}
}
