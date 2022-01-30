package fr.raksrinana.rsndiscord.interaction.command.user.base;

import fr.raksrinana.rsndiscord.interaction.command.slash.api.ISimpleSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.ExecutableSlashCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleUserCommand extends ExecutableSlashCommand implements ISimpleSlashCommand{
	@Override
	@NotNull
	public CommandData getDefinition(){
		return Commands.user(getId())
				.setDefaultEnabled(getDefaultPermission());
	}
}
