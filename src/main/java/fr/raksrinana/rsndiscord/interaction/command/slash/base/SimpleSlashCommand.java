package fr.raksrinana.rsndiscord.interaction.command.slash.base;

import fr.raksrinana.rsndiscord.interaction.command.slash.api.ISimpleSlashCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleSlashCommand extends ExecutableSlashCommand implements ISimpleSlashCommand{
	@Override
	@NotNull
	public CommandData getDefinition(){
		return Commands.slash(getId(), getShortDescription())
				//.setLocalizationFunction(InteractionService.getLocalizationFunction())
				.setGuildOnly(isGuildOnly())
				.setDefaultPermissions(getDefaultPermission())
				.addOptions(getOptions());
	}
}
