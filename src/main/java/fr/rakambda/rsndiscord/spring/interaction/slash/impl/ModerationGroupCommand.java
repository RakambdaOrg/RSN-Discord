package fr.rakambda.rsndiscord.spring.interaction.slash.impl;

import fr.rakambda.rsndiscord.spring.interaction.slash.api.IRegistrableSlashCommand;
import fr.rakambda.rsndiscord.spring.interaction.slash.impl.moderation.ClearCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import static net.dv8tion.jda.api.interactions.commands.OptionType.CHANNEL;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@Configuration
public class ModerationGroupCommand implements IRegistrableSlashCommand{
	@Override
	@NotNull
	public CommandData getDefinition(@NotNull LocalizationFunction localizationFunction){
		return Commands.slash("mod", "Moderation commands")
				.setLocalizationFunction(localizationFunction)
				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL))
				.setGuildOnly(true)
				.addSubcommands(
						new SubcommandData("clear", "Clear messages from a channel")
								.addOptions(
										new OptionData(CHANNEL, ClearCommand.CHANNEL_OPTION_ID, "Channel to delete the message in (default: current channel)")
												.setChannelTypes(ChannelType.TEXT),
										new OptionData(INTEGER, ClearCommand.MESSAGE_COUNT_OPTION_ID, "Number of messages to delete"),
										new OptionData(STRING, ClearCommand.ORDER_OPTION_ID, "Clear order (default: new)")
												.addChoice("Newest first", ClearCommand.ORDER_NEW_CHOICE)
												.addChoice("Oldest first", ClearCommand.ORDER_OLD_CHOICE))
				);
	}
}
