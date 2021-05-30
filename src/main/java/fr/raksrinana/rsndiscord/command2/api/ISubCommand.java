package fr.raksrinana.rsndiscord.command2.api;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

public interface ISubCommand extends IExecutableCommand{
	@NotNull
	SubcommandData getSlashCommand();
}
