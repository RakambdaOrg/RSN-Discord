package fr.raksrinana.rsndiscord.interaction.command.slash.impl.moderation;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static net.dv8tion.jda.api.interactions.commands.OptionType.CHANNEL;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

public class ClearCommand extends SubSlashCommand{
	public static final String CHANNEL_OPTION_ID = "channel";
	public static final String MESSAGE_COUNT_OPTION_ID = "count";
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return Set.of(
				new OptionData(CHANNEL, CHANNEL_OPTION_ID, "Channel to delete the message in (default: current channel)")
						.setChannelTypes(ChannelType.TEXT),
				new OptionData(INTEGER, MESSAGE_COUNT_OPTION_ID, "Number of messages to delete"));
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var channel = event.getChannel();
		
		var messageCount = getOptionAsInt(event.getOption(MESSAGE_COUNT_OPTION_ID)).orElse(100);
		var targetChannel = Optional.ofNullable(event.getOption(CHANNEL_OPTION_ID))
				.map(OptionMapping::getAsMessageChannel)
				.map(MessageChannel.class::cast)
				.orElse(channel);
		
		var message = translate(event.getGuild(), "clear.removing", messageCount, targetChannel.getId());
		JDAWrappers.edit(event, message).submitAndDelete(5);
		
		targetChannel.getIterableHistory()
				.takeAsync(messageCount)
				.thenAccept(messages -> messages.forEach(m -> JDAWrappers.delete(m).submit()));
		return HANDLED;
	}
	
	@Override
	@NotNull
	public String getId(){
		return "clear";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Clear messages from a channel";
	}
}