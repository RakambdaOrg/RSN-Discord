package fr.raksrinana.rsndiscord.interaction.command.user.api;

import fr.raksrinana.rsndiscord.interaction.command.api.IExecutableCommand;
import fr.raksrinana.rsndiscord.interaction.command.api.IRegistrableCommand;
import net.dv8tion.jda.api.interactions.commands.context.UserContextInteraction;

public interface ISimpleUserCommand extends IExecutableCommand<UserContextInteraction>, IRegistrableCommand{
}
