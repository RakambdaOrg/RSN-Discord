package fr.raksrinana.rsndiscord.interaction.command.slash.impl.moderation;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static net.dv8tion.jda.api.interactions.commands.OptionType.CHANNEL;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@Log4j2
public class ClearCommand extends SubSlashCommand{
	private static final String CHANNEL_OPTION_ID = "channel";
	private static final String MESSAGE_COUNT_OPTION_ID = "count";
	private static final String ORDER_OPTION_ID = "order";
	private static final String OLD_ORDER = "old";
	private static final String NEW_ORDER = "new";
	private static final int DEFAULT_COUNT = 100;
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return Set.of(
				new OptionData(CHANNEL, CHANNEL_OPTION_ID, "Channel to delete the message in (default: current channel)")
						.setChannelTypes(ChannelType.TEXT),
				new OptionData(INTEGER, MESSAGE_COUNT_OPTION_ID, "Number of messages to delete (default " + DEFAULT_COUNT + ")"),
				new OptionData(STRING, ORDER_OPTION_ID, "Clear order (default: new)")
						.addChoice("Newest first", NEW_ORDER)
						.addChoice("Oldest first", OLD_ORDER)
		);
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var channel = event.getChannel();
		
		var messageCount = getOptionAsInt(event.getOption(MESSAGE_COUNT_OPTION_ID)).orElse(DEFAULT_COUNT);
		var targetChannel = Optional.ofNullable(event.getOption(CHANNEL_OPTION_ID))
				.map(OptionMapping::getAsMessageChannel)
				.map(MessageChannel.class::cast)
				.orElse(channel);
		var order = Optional.ofNullable(event.getOption(ORDER_OPTION_ID))
				.map(OptionMapping::getAsString)
				.map(OLD_ORDER::equals)
				.orElse(false) ? PaginationAction.PaginationOrder.FORWARD : PaginationAction.PaginationOrder.BACKWARD;
		
		JDAWrappers.edit(event, translate(event.getGuild(), "clear.removing", messageCount, targetChannel.getId())).submitAndDelete(5)
				.thenCompose(msg -> JDAWrappers.history(targetChannel)
						.order(order)
						.takeAsync(messageCount)
						.thenCompose(messages -> deleteAll(event, messages))
						.thenCompose(empty -> JDAWrappers.edit(event, "Clear messages done").submit()));
		return HANDLED;
	}
	
	@NotNull
	private CompletableFuture<Void> deleteAll(@NotNull SlashCommandInteraction event, @NotNull Collection<Message> messages){
		var size = messages.size();
		var counter = new AtomicInteger(0);
		
		Consumer<Void> notifier = empty -> {
			var value = counter.incrementAndGet();
			if(value % 5 == 0 || value == size){
				log.info("Deleting messages in {} processed {}/{}", event.getChannel(), value, size);
			}
		};
		
		return messages.stream()
				.map(this::processMessage)
				.map(f -> f.thenAccept(notifier))
				.reduce((left, right) -> left.thenCombine(right, (v1, v2) -> null))
				.orElseGet(() -> CompletableFuture.completedFuture(null));
	}
	
	@NotNull
	private CompletableFuture<Void> processMessage(@NotNull Message message){
		return deleteThread(message.getStartedThread()).thenCompose(empty -> JDAWrappers.delete(message).submit());
	}
	
	@NotNull
	private CompletableFuture<Void> deleteThread(@Nullable ThreadChannel thread){
		if(Objects.isNull(thread)){
			return CompletableFuture.completedFuture(null);
		}
		return JDAWrappers.delete(thread).submit();
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
