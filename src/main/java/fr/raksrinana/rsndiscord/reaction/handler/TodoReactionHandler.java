package fr.raksrinana.rsndiscord.reaction.handler;

import fr.raksrinana.rsndiscord.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.settings.impl.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
import static fr.raksrinana.rsndiscord.reaction.ReactionTag.TODO;
import static fr.raksrinana.rsndiscord.reaction.ReactionUtils.DELETE_KEY;
import static fr.raksrinana.rsndiscord.reaction.handler.ReactionHandlerResult.*;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.*;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.SECONDS;

@ReactionHandler
@Log4j2
public class TodoReactionHandler implements IReactionHandler{
	@Override
	public boolean acceptTag(@NotNull ReactionTag tag){
		return Objects.equals(tag, TODO);
	}
	
	@Override
	@NotNull
	public ReactionHandlerResult accept(@NotNull MessageReactionAddEvent event, @NotNull WaitingReactionMessageConfiguration reaction){
		var reactionEmote = event.getReactionEmote();
		
		if(reactionEmote.isEmoji()){
			var emote = BasicEmotes.getEmote(reactionEmote.getEmoji());
			if(isValidEmote(emote)){
				try{
					return processTodoCompleted(event, emote, reaction);
				}
				catch(InterruptedException | ExecutionException | TimeoutException e){
					Utilities.reportException("Failed to handle reaction", e);
					log.error("Failed to handle reaction", e);
					return FAIL;
				}
			}
		}
		return PROCESSED;
	}
	
	protected boolean isValidEmote(@NotNull BasicEmotes emote){
		return emote == CHECK_OK || emote == PAPERCLIP || emote == RIGHT_ARROW_CURVING_LEFT;
	}
	
	@NotNull
	protected ReactionHandlerResult processTodoCompleted(@NotNull MessageReactionAddEvent event, @NotNull BasicEmotes emote, @NotNull WaitingReactionMessageConfiguration todo) throws InterruptedException, ExecutionException, TimeoutException{
		var user = event.retrieveUser().submit().get(30, SECONDS);
		return todo.getMessage().getMessage()
				.map(message -> {
					if(emote == PAPERCLIP){
						return handleArchive(event, user, message);
					}
					
					if(emote == RIGHT_ARROW_CURVING_LEFT){
						return handleReply(event, user, message);
					}
					
					if(message.isPinned()){
						JDAWrappers.unpin(message).submit();
					}
					if(ofNullable(todo.getData().get(DELETE_KEY)).map(Boolean::parseBoolean).orElse(false)){
						return handleDelete(event, user, message);
					}
					
					JDAWrappers.edit(message, OK_HAND.getValue() + " __**DONE**__:  " + message.getContentRaw()).submit();
					JDAWrappers.clearReactions(message).submit();
					return PROCESSED_DELETE;
				}).orElse(PROCESSED);
	}
	
	private ReactionHandlerResult handleDelete(@NotNull MessageReactionAddEvent event, @NotNull User user, @NotNull Message message){
		var threadName = "reply-" + message.getIdLong();
		
		try{
			return Optional.ofNullable(message.getMessageReference())
					.map(reference -> reference.resolve().submit().thenApply(m -> JDAWrappers.delete(m).submit()))
					.orElseGet(() -> CompletableFuture.completedFuture(null))
					.thenCompose(empty -> Utilities.getThreadByName(event.getGuild(), threadName))
					.thenCompose(thread -> JDAWrappers.delete(thread).submit())
					.exceptionally(throwable -> {
						log.error("Failed to deleted thread {}", threadName, throwable);
						return null;
					})
					.thenCompose(empty -> JDAWrappers.delete(message).submit())
					.thenApply(empty -> PROCESSED_DELETE)
					.get(30, SECONDS);
		}
		catch(InterruptedException | ExecutionException | TimeoutException e){
			log.error("Failed to delete reply", e);
		}
		return PROCESSED;
	}
	
	@NotNull
	private ReactionHandlerResult handleArchive(@NotNull MessageReactionAddEvent event, @NotNull User user, @NotNull Message message){
		JDAWrappers.removeAllReactions(message, CHECK_OK.getValue()).submit();
		JDAWrappers.removeAllReactions(message, PAPERCLIP.getValue()).submit();
		return PROCESSED;
	}
	
	@NotNull
	private ReactionHandlerResult handleReply(@NotNull MessageReactionAddEvent event, @NotNull User user, @NotNull Message message){
		var guild = event.getGuild();
		var replyName = "reply-" + event.getMessageIdLong();
		
		try{
			return JDAWrappers.createThread(message, replyName).submit()
					.thenCompose(thread -> {
						var authorFuture = Stream.of(JDAWrappers.addThreadMember(thread, user).submit());
						var mentionedFutures = message.getMentionedMembers().stream()
								.map(u -> JDAWrappers.addThreadMember(thread, u).submit());
						
						var futures = Stream.concat(authorFuture, mentionedFutures).toArray(CompletableFuture[]::new);
						
						return CompletableFuture.allOf(
								CompletableFuture.allOf(futures),
								JDAWrappers.message(thread, translate(guild, "reaction.react-archive", user.getAsMention())).submit(),
								JDAWrappers.removeAllReactions(event.getReaction()).submit()
						);
					})
					.thenApply(e -> PROCESSED)
					.exceptionally(ex -> PROCESSED)
					.get(30, SECONDS);
		}
		catch(InterruptedException | ExecutionException | TimeoutException e){
			log.error("Failed to create reply channel", e);
		}
		return PROCESSED;
	}
	
	@Override
	public int getPriority(){
		return 1000;
	}
}
