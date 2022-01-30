package fr.raksrinana.rsndiscord.interaction.command.slash.api;

import fr.raksrinana.rsndiscord.interaction.command.api.IExecutableCommand;
import fr.raksrinana.rsndiscord.interaction.command.api.IRegistrableCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public interface ISimpleSlashCommand extends IExecutableCommand<SlashCommandInteraction>, IRegistrableCommand{
}
