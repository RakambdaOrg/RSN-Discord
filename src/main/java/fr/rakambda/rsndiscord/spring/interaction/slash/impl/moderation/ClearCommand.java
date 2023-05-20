package fr.rakambda.rsndiscord.spring.interaction.slash.impl.moderation;

import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@Component
public class ClearCommand implements IExecutableSlashCommandGuild{
	public static final String CHANNEL_OPTION_ID = "channel";
	public static final String MESSAGE_COUNT_OPTION_ID = "count";
	public static final String ORDER_OPTION_ID = "order";
	
	public static final String ORDER_OLD_CHOICE = "old";
	public static final String ORDER_NEW_CHOICE = "new";
	
	private static final int DEFAULT_COUNT = 100;
	
	private final LocalizationService localizationService;
	
	@Autowired
	public ClearCommand(LocalizationService localizationService){
		this.localizationService = localizationService;
	}
	
	@Override
	@NotNull
	public String getId(){
		return "clear";
	}
	
	@Override
	@NotNull
	public String getPath(){
		return "mod/clear";
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var deferred = event.deferReply(true).submit();
		
		var channel = event.getGuildChannel();
		
		var messageCount = getOptionAsInt(event.getOption(MESSAGE_COUNT_OPTION_ID)).orElse(DEFAULT_COUNT);
		var targetChannel = Optional.ofNullable(event.getOption(CHANNEL_OPTION_ID))
				.map(OptionMapping::getAsChannel)
				.map(GuildChannelUnion::asGuildMessageChannel)
				.orElse(channel);
		var order = Optional.ofNullable(event.getOption(ORDER_OPTION_ID))
				.map(OptionMapping::getAsString)
				.map(ORDER_OLD_CHOICE::equals)
				.orElse(false) ? PaginationAction.PaginationOrder.FORWARD : PaginationAction.PaginationOrder.BACKWARD;
		
		var content = localizationService.translate(event.getUserLocale(), "clear.removing", messageCount, targetChannel.getId());
		return deferred
				.thenCompose(empty -> JDAWrappers.edit(event, content).submit())
				.thenCompose(msg -> JDAWrappers.history(targetChannel)
						.order(order)
						.takeAsync(messageCount))
				.thenCompose(messages -> deleteAll(event, messages))
				.thenCompose(empty -> JDAWrappers.edit(event, "Clear messages done").submit());
	}
	
	@NotNull
	private CompletableFuture<Void> deleteAll(@NotNull SlashCommandInteraction event, @NotNull Collection<Message> messages){
		var size = messages.size();
		var counter = new AtomicInteger(0);
		
		log.info("Found {} messages to delete", messages.size());
		
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
		var thread = message.getStartedThread();
		if(Objects.isNull(thread)){
			return JDAWrappers.delete(message).submit();
		}
		
		return JDAWrappers.delete(thread).submit().thenCompose(empty -> JDAWrappers.delete(message).submit());
	}
}
