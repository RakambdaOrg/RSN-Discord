package fr.raksrinana.rsndiscord.interaction.command.slash.api;

import fr.raksrinana.rsndiscord.interaction.command.api.ICommand;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;

public interface ISubGroupSlashCommand extends ICommand{
	@NotNull
	SubcommandGroupData getSlashCommand();
}
