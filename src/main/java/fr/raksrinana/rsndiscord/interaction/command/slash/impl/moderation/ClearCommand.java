package fr.raksrinana.rsndiscord.interaction.command.slash.impl.moderation;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import fr.raksrinana.rsndiscord.interaction.component.button.impl.ClearDeleteThreadCancelButtonHandler;
import fr.raksrinana.rsndiscord.schedule.impl.DeleteThreadScheduleHandler;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.time.ZonedDateTime.now;
import static net.dv8tion.jda.api.interactions.commands.OptionType.BOOLEAN;
import static net.dv8tion.jda.api.interactions.commands.OptionType.CHANNEL;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;

@Log4j2
public class ClearCommand extends SubSlashCommand{
	public static final String CHANNEL_OPTION_ID = "channel";
	public static final String MESSAGE_COUNT_OPTION_ID = "count";
	public static final String ORDER_OPTION_ID = "order";
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return Set.of(
				new OptionData(CHANNEL, CHANNEL_OPTION_ID, "Channel to delete the message in (default: current channel)")
						.setChannelTypes(ChannelType.TEXT),
				new OptionData(INTEGER, MESSAGE_COUNT_OPTION_ID, "Number of messages to delete"),
				new OptionData(BOOLEAN, ORDER_OPTION_ID, "Clear from bottom (most recent, default: false)")
		);
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
		var order = Optional.ofNullable(event.getOption(ORDER_OPTION_ID))
				.map(OptionMapping::getAsBoolean)
				.orElse(false) ? PaginationAction.PaginationOrder.BACKWARD : PaginationAction.PaginationOrder.FORWARD;
		
		JDAWrappers.edit(event, translate(event.getGuild(), "clear.removing", messageCount, targetChannel.getId())).submitAndDelete(5)
				.thenCompose(msg -> targetChannel.getIterableHistory()
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
		
		return CompletableFuture.allOf(messages.stream()
				.map(this::processMessage)
				.map(f -> f.thenAccept(notifier))
				.toArray(CompletableFuture[]::new));
	}
	
	@NotNull
	private CompletableFuture<Void> processMessage(@NotNull Message message){
		CompletableFuture<Void> deleteThread = CompletableFuture.completedFuture(null);
		var thread = message.getStartedThread();
		if(Objects.nonNull(thread)){
			deleteThread = JDAWrappers.message(thread, "Deleting this thread soon")
					.addActionRow(new ClearDeleteThreadCancelButtonHandler().asComponent())
					.submit()
					.thenAccept(m -> Settings.get(message.getGuild()).add(new DeleteThreadScheduleHandler(thread.getIdLong(), now().plusHours(3))));
		}
		
		return deleteThread.thenCompose(empty -> JDAWrappers.delete(message).submit());
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
