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
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class ClearRangeCommand implements IExecutableSlashCommandGuild{
	public static final String CHANNEL_OPTION_ID = "channel";
	public static final String FROM_OPTION_ID = "from";
	public static final String TO_OPTION_ID = "to";
	
	private final LocalizationService localizationService;
	
	@Autowired
	public ClearRangeCommand(LocalizationService localizationService){
		this.localizationService = localizationService;
	}
	
	@Override
	@NotNull
	public String getId(){
		return "clear-range";
	}
	
	@Override
	@NotNull
	public String getPath(){
		return "mod/clear-range";
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var deferred = event.deferReply(true).submit();
		
		var channel = event.getGuildChannel();
		
		var targetChannel = Optional.ofNullable(event.getOption(CHANNEL_OPTION_ID))
				.map(OptionMapping::getAsChannel)
				.map(GuildChannelUnion::asGuildMessageChannel)
				.orElse(channel);
		var optionFromId = event.getOption(FROM_OPTION_ID).getAsLong();
		var optionToId = event.getOption(TO_OPTION_ID).getAsLong();
		
		var fromMessage = targetChannel.retrieveMessageById(optionFromId).complete();
		var toMessage = targetChannel.retrieveMessageById(optionToId).complete();
		
		long fromId;
		long toId;
		if(toMessage.getTimeCreated().isAfter(fromMessage.getTimeCreated())){
			fromId = toMessage.getIdLong();
			toId = fromMessage.getIdLong();
		}
		else{
			fromId = fromMessage.getIdLong();
			toId = toMessage.getIdLong();
		}
		
		return deferred
				.thenCompose(empty -> JDAWrappers.edit(event, "Doing stuff").submit())
				.thenAccept(msg -> JDAWrappers.history(targetChannel)
						.skipTo(fromId)
						.foreachAsync(m -> {
							processMessage(m);
							return m.getIdLong() != toId;
						}));
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
