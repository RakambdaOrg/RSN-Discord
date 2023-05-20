package fr.rakambda.rsndiscord.spring.interaction.slash.impl;

import fr.rakambda.rsndiscord.spring.interaction.slash.api.IRegistrableSlashCommand;
import fr.rakambda.rsndiscord.spring.interaction.slash.impl.moderation.ClearCommand;
import fr.rakambda.rsndiscord.spring.interaction.slash.impl.moderation.ClearRangeCommand;
import fr.rakambda.rsndiscord.spring.interaction.slash.impl.moderation.ClearThreadsCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
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
												.addChoice("Oldest first", ClearCommand.ORDER_OLD_CHOICE)),
						new SubcommandData("clear-range", "Clear messages within a range from a channel")
								.addOptions(
										new OptionData(CHANNEL, ClearRangeCommand.CHANNEL_OPTION_ID, "Channel to delete the message in (default: current channel)")
												.setChannelTypes(ChannelType.TEXT),
										new OptionData(OptionType.STRING, ClearRangeCommand.FROM_OPTION_ID, "Start message")
												.setRequired(true),
										new OptionData(OptionType.STRING, ClearRangeCommand.TO_OPTION_ID, "End message")
												.setRequired(true)),
						new SubcommandData("clear-threads", "Clear threads in a forum")
								.addOptions(
										new OptionData(CHANNEL, ClearThreadsCommand.CHANNEL_OPTION_ID, "Channel to delete the threads in")
												.setChannelTypes(ChannelType.FORUM)
												.setRequired(true),
										new OptionData(OptionType.STRING, ClearThreadsCommand.EXCLUDE_OPTION_ID, "Exclude tags (separated by comma, id or name)")),
						new SubcommandData("tags", "Obtain thread channel's tags")
								.addOptions(
										new OptionData(CHANNEL, ClearCommand.CHANNEL_OPTION_ID, "Thread to get the tags from")
												.setChannelTypes(ChannelType.GUILD_PUBLIC_THREAD)
												.setRequired(true))
				);
	}
}
