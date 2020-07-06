package fr.raksrinana.rsndiscord.utils.reaction;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.*;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@Slf4j
public class TodoReactionHandler implements ReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, ReactionTag.TODO);
	}
	
	@Override
	public ReactionHandlerResult accept(@NonNull GuildMessageReactionAddEvent event, @NonNull WaitingReactionMessageConfiguration reaction){
		if(event.getReactionEmote().isEmoji()){
			final var emote = BasicEmotes.getEmote(event.getReactionEmote().getEmoji());
			if(isValidEmote(emote)){
				return processTodoCompleted(event, emote, reaction);
			}
		}
		return ReactionHandlerResult.PROCESSED;
	}
	
	protected boolean isValidEmote(@NonNull BasicEmotes emote){
		return emote == CHECK_OK || emote == PAPERCLIP || emote == RIGHT_ARROW_CURVING_LEFT;
	}
	
	protected ReactionHandlerResult processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull BasicEmotes emote, @NonNull WaitingReactionMessageConfiguration todo){
		return todo.getMessage().getMessage().map(message -> {
			if(emote == PAPERCLIP){
				final var forwarded = Optional.ofNullable(Settings.get(event.getGuild()).getReactionsConfiguration().getSavedForwarding().get(new ChannelConfiguration(event.getChannel()))).flatMap(ChannelConfiguration::getChannel).map(forwardChannel -> {
					try{
						return Actions.forwardMessage(message, forwardChannel).thenApply(forwardedMessage -> true).exceptionally(e -> false).get(30, TimeUnit.SECONDS);
					}
					catch(InterruptedException | ExecutionException | TimeoutException e){
						log.error("Failed to forward message", e);
					}
					return false;
				}).orElse(false);
				if(!forwarded){
					Actions.sendPrivateMessage(event.getGuild(), event.getUser(), translate(event.getGuild(), "reaction.not-configured"), null);
					Actions.removeReaction(event.getReaction(), event.getUser());
					return ReactionHandlerResult.PROCESSED;
				}
			}
			if(emote == RIGHT_ARROW_CURVING_LEFT){
				event.getGuild().createTextChannel("reply-" + event.getMessageIdLong()).submit().thenAccept(forwardChannel -> {
					Optional.ofNullable(message.getTextChannel().getParent()).ifPresent(category -> Actions.setCategoryAndSync(forwardChannel, category));
					
					Actions.sendMessage(forwardChannel, translate(event.getGuild(), "reaction.original-from", message.getAuthor().getAsMention()), null);
					Actions.forwardMessage(message, forwardChannel);
					Actions.sendMessage(forwardChannel, translate(event.getGuild(), "reaction.react-archive", event.getMember().getAsMention(), CROSS_NO.getValue()), null).thenAccept(message1 -> {
						Actions.addReaction(message1, CROSS_NO.getValue());
						Settings.get(event.getGuild()).addMessagesAwaitingReaction(new WaitingReactionMessageConfiguration(message1, ReactionTag.DELETE_CHANNEL));
					});
				});
			}
			if(message.isPinned()){
				Actions.unpin(message);
			}
			if(Optional.ofNullable(todo.getData().get(ReactionUtils.DELETE_KEY)).map(Boolean::parseBoolean).orElse(false)){
				Actions.deleteMessage(message);
			}
			else{
				Actions.editMessage(message, BasicEmotes.OK_HAND.getValue() + " __**DONE**__:  " + message.getContentRaw());
				Actions.clearReactions(message);
			}
			return ReactionHandlerResult.PROCESSED_DELETE;
		}).orElse(ReactionHandlerResult.PROCESSED);
	}
	
	@Override
	public int getPriority(){
		return 1000;
	}
}
