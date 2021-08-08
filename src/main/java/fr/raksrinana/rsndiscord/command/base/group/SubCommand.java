package fr.raksrinana.rsndiscord.command.base.group;

import fr.raksrinana.rsndiscord.command.api.ISubCommand;
import fr.raksrinana.rsndiscord.command.base.ExecutableCommand;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

public abstract class SubCommand extends ExecutableCommand implements ISubCommand{
	@Override
	@NotNull
	public SubcommandData getSlashCommand(){
		return new SubcommandData(getId(), getShortDescription())
				.addOptions(getOptions());
	}
}
