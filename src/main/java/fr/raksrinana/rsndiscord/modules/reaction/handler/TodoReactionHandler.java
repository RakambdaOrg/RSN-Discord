package fr.raksrinana.rsndiscord.modules.reaction.handler;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.modules.reaction.config.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import static fr.raksrinana.rsndiscord.modules.reaction.ReactionTag.DELETE_CHANNEL;
import static fr.raksrinana.rsndiscord.modules.reaction.ReactionTag.TODO;
import static fr.raksrinana.rsndiscord.modules.reaction.ReactionUtils.DELETE_KEY;
import static fr.raksrinana.rsndiscord.modules.reaction.handler.ReactionHandlerResult.*;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.*;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.SECONDS;

@ReactionHandler
public class TodoReactionHandler implements IReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, TODO);
	}
	
	@Override
	public ReactionHandlerResult accept(@NonNull GuildMessageReactionAddEvent event, @NonNull WaitingReactionMessageConfiguration reaction){
		var reactionEmote = event.getReactionEmote();
		
		if(reactionEmote.isEmoji()){
			final var emote = BasicEmotes.getEmote(reactionEmote.getEmoji());
			if(isValidEmote(emote)){
				try{
					return processTodoCompleted(event, emote, reaction);
				}
				catch(InterruptedException | ExecutionException | TimeoutException e){
					Utilities.reportException("Failed to handle reaction", e);
					Log.getLogger(event.getGuild()).error("Failed to handle reaction", e);
					return FAIL;
				}
			}
		}
		return PROCESSED;
	}
	
	protected boolean isValidEmote(@NonNull BasicEmotes emote){
		return emote == CHECK_OK || emote == PAPERCLIP || emote == RIGHT_ARROW_CURVING_LEFT;
	}
	
	protected ReactionHandlerResult processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull BasicEmotes emote, @NonNull WaitingReactionMessageConfiguration todo) throws InterruptedException, ExecutionException, TimeoutException{
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
						message.unpin().submit();
					}
					if(ofNullable(todo.getData().get(DELETE_KEY)).map(Boolean::parseBoolean).orElse(false)){
						message.delete().submit();
					}
					else{
						message.editMessage(OK_HAND.getValue() + " __**DONE**__:  " + message.getContentRaw()).submit();
						message.clearReactions().submit();
					}
					return PROCESSED_DELETE;
				}).orElse(PROCESSED);
	}
	
	private ReactionHandlerResult handleArchive(@NonNull GuildMessageReactionAddEvent event, User user, Message message){
		var guild = event.getGuild();
		
		var forwarded = ofNullable(Settings.get(guild)
				.getReactionsConfiguration()
				.getSavedForwarding()
				.get(new ChannelConfiguration(event.getChannel())))
				.flatMap(ChannelConfiguration::getChannel)
				.map(forwardChannel -> {
					try{
						return forwardChannel.sendMessage(message).submit()
								.thenApply(forwardedMessage -> true)
								.exceptionally(e -> false)
								.get(30, SECONDS);
					}
					catch(InterruptedException | ExecutionException | TimeoutException e){
						Log.getLogger(guild).error("Failed to forward message", e);
					}
					return false;
				}).orElse(false);
		
		if(!forwarded){
			event.getReaction().removeReaction().submit();
			user.openPrivateChannel().submit()
					.thenAccept(privateChannel -> privateChannel.sendMessage(translate(guild, "reaction.not-configured")).submit());
			return PROCESSED;
		}
		
		message.delete().submit();
		return PROCESSED_DELETE;
	}
	
	private ReactionHandlerResult handleReply(@NonNull GuildMessageReactionAddEvent event, User user, Message message){
		var guild = event.getGuild();
		
		try{
			return guild.createTextChannel("reply-" + event.getMessageIdLong()).submit()
					.thenApply(forwardChannel -> {
						ofNullable(message.getTextChannel().getParent())
								.ifPresent(category -> forwardChannel.getManager().setParent(category)
										.sync(category)
										.submit());
						forwardChannel.sendMessage(translate(guild, "reaction.original-from", message.getAuthor().getAsMention())).submit()
								.thenCompose(sent -> forwardChannel.sendMessage(message).submit())
								.thenCompose(sent -> forwardChannel.sendMessage(translate(guild, "reaction.react-archive", user.getAsMention(), CROSS_NO.getValue())).submit()
										.thenAccept(forwarded -> {
											forwarded.addReaction(CROSS_NO.getValue()).submit();
											Settings.get(guild).addMessagesAwaitingReaction(new WaitingReactionMessageConfiguration(forwarded, DELETE_CHANNEL));
											message.delete().submit();
										}));
						return PROCESSED_DELETE;
					})
					.exceptionally(ex -> PROCESSED)
					.get(30, SECONDS);
		}
		catch(InterruptedException | ExecutionException | TimeoutException e){
			Log.getLogger(guild).error("Failed to create reply channel", e);
		}
		return PROCESSED;
	}
	
	@Override
	public int getPriority(){
		return 1000;
	}
}
