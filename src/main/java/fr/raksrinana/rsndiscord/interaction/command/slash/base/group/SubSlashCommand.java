package fr.raksrinana.rsndiscord.interaction.command.slash.base.group;

import fr.raksrinana.rsndiscord.interaction.command.slash.api.ISubSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.ExecutableSlashCommand;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

public abstract class SubSlashCommand extends ExecutableSlashCommand implements ISubSlashCommand{
	@Override
	@NotNull
	public SubcommandData getSlashCommand(){
		return new SubcommandData(getId(), getShortDescription())
				.addOptions(getOptions());
	}
}
