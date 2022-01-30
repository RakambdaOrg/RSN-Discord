package fr.raksrinana.rsndiscord.interaction.command.slash.api;

import fr.raksrinana.rsndiscord.interaction.command.api.IExecutableCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

public interface ISubSlashCommand extends IExecutableCommand<SlashCommandInteraction>{
	@NotNull
	SubcommandData getSlashCommand();
}
