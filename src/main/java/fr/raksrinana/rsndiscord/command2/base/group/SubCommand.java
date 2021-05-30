package fr.raksrinana.rsndiscord.command2.base.group;

import fr.raksrinana.rsndiscord.command2.api.ISubCommand;
import fr.raksrinana.rsndiscord.command2.base.ExecutableCommand;
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
