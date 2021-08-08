package fr.raksrinana.rsndiscord.command.impl.moderation;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static net.dv8tion.jda.api.interactions.commands.OptionType.CHANNEL;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

public class ClearCommand extends SubCommand{
	public static final String CHANNEL_OPTION_ID = "channel";
	public static final String MESSAGE_COUNT_OPTION_ID = "count";
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return Set.of(
				new OptionData(CHANNEL, CHANNEL_OPTION_ID, "Channel to delete the message in (default: current channel)"),
				new OptionData(INTEGER, MESSAGE_COUNT_OPTION_ID, "Number of messages to delete"));
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var channel = event.getChannel();
		
		var messageCount = getOptionAsInt(event.getOption(MESSAGE_COUNT_OPTION_ID)).orElse(100);
		var targetChannel = Optional.ofNullable(event.getOption(CHANNEL_OPTION_ID)).map(OptionMapping::getAsMessageChannel).orElse(channel);
		
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
