package fr.rakambda.rsndiscord.spring.interaction.button.impl.weward;

import fr.rakambda.rsndiscord.spring.interaction.button.api.IExecutableButtonGuild;
import fr.rakambda.rsndiscord.spring.interaction.exception.InvalidChannelTypeException;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Component
public class WewardDeleteButtonHandler implements IExecutableButtonGuild{
	private static final String COMPONENT_ID = "weward-delete";
	
	private final LocalizationService localizationService;
	
	@Autowired
	public WewardDeleteButtonHandler(LocalizationService localizationService){
		this.localizationService = localizationService;
	}
	
	@Override
	@NonNull
	public String getComponentId(){
		return COMPONENT_ID;
	}
	
	@NonNull
	@Override
	public CompletableFuture<?> executeGuild(@NonNull ButtonInteraction event, @NonNull Guild guild, @NonNull Member member) throws InvalidChannelTypeException{
		var channel = event.getChannel();
		var notOwnerContent = localizationService.translate(event.getGuildLocale(), "weward.card-delete-not-owner");
		
		if(event.getChannelType() == ChannelType.GUILD_PUBLIC_THREAD){
			var threadChannel = channel.asThreadChannel();
			return threadChannel.retrieveParentMessage().submit()
					.thenApply(parentMessage -> {
						if(getOwner(parentMessage).map(u -> !Objects.equals(u.getId(), member.getId())).orElse(false)){
							var deferred = event.deferReply(true).submit();
							return deferred.thenCompose(empty -> JDAWrappers.reply(event, notOwnerContent).submit());
						}
						var deferred = event.deferEdit().submit();
						return deleteThread(threadChannel, deferred);
					});
		}
		if(event.getChannelType() == ChannelType.TEXT){
			if(getOwner(event.getMessage()).map(u -> !Objects.equals(u.getId(), member.getId())).orElse(false)){
				var deferred = event.deferReply(true).submit();
				return deferred.thenCompose(empty -> JDAWrappers.reply(event, notOwnerContent).submit());
			}
			var deferred = event.deferEdit().submit();
			return deferred.thenCompose(empty -> JDAWrappers.delete(event.getMessage()).submit());
		}
		
		throw new InvalidChannelTypeException(event.getChannelType());
	}
	
	@NonNull
	private Optional<UserSnowflake> getOwner(@NonNull Message message){
		return message.getEmbeds().stream()
				.filter(Objects::nonNull)
				.map(MessageEmbed::getFooter)
				.filter(Objects::nonNull)
				.map(MessageEmbed.Footer::getText)
				.filter(Objects::nonNull)
				.findFirst()
				.map(User::fromId);
	}
	
	@NonNull
	private CompletableFuture<Void> deleteThread(@NonNull ThreadChannel threadChannel, @NonNull CompletableFuture<InteractionHook> deferred){
		if(threadChannel.getParentChannel().getType() == ChannelType.FORUM){
			return deferred.thenCompose(empty -> JDAWrappers.delete(threadChannel).submit());
		}
		
		return deferred.thenCompose(m -> handleThreadChannel(threadChannel));
	}
	
	@NonNull
	private CompletableFuture<Void> handleThreadChannel(@NonNull ThreadChannel threadChannel){
		return threadChannel.retrieveParentMessage().submit()
				.thenCompose(message -> JDAWrappers.delete(message).submit())
				.exceptionally(throwable -> {
					log.warn("Failed to delete thread parent message", throwable);
					return null;
				})
				.thenCompose(empty -> JDAWrappers.delete(threadChannel).submit());
	}
	
	@NonNull
	public static Supplier<Button> builder(){
		return () -> Button.danger(COMPONENT_ID, "No longer available").withEmoji(Emoji.fromUnicode("U+1F5D1"));
	}
}
