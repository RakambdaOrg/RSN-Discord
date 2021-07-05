package fr.raksrinana.rsndiscord.reaction.handler;

import fr.raksrinana.rsndiscord.components.impl.button.ReplyChannelDeleteButtonHandler;
import fr.raksrinana.rsndiscord.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
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
	public ReactionHandlerResult accept(@NotNull GuildMessageReactionAddEvent event, @NotNull WaitingReactionMessageConfiguration reaction){
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
	protected ReactionHandlerResult processTodoCompleted(@NotNull GuildMessageReactionAddEvent event, @NotNull BasicEmotes emote, @NotNull WaitingReactionMessageConfiguration todo) throws InterruptedException, ExecutionException, TimeoutException{
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
						JDAWrappers.delete(message).submit();
					}
					else{
						JDAWrappers.edit(message, OK_HAND.getValue() + " __**DONE**__:  " + message.getContentRaw()).submit();
						JDAWrappers.clearReactions(message).submit();
					}
					return PROCESSED_DELETE;
				}).orElse(PROCESSED);
	}
	
	@NotNull
	private ReactionHandlerResult handleArchive(@NotNull GuildMessageReactionAddEvent event, @NotNull User user, @NotNull Message message){
		var guild = event.getGuild();
		
		var forwarded = ofNullable(Settings.get(guild)
				.getReactionsConfiguration()
				.getSavedForwarding()
				.get(new ChannelConfiguration(event.getChannel())))
				.flatMap(ChannelConfiguration::getChannel)
				.map(forwardChannel -> {
					try{
						return JDAWrappers.message(forwardChannel, message).submit()
								.thenApply(forwardedMessage -> true)
								.exceptionally(e -> false)
								.get(30, SECONDS);
					}
					catch(InterruptedException | ExecutionException | TimeoutException e){
						log.error("Failed to forward message", e);
					}
					return false;
				}).orElse(false);
		
		if(!forwarded){
			JDAWrappers.removeReaction(event.getReaction()).submit();
			user.openPrivateChannel().submit()
					.thenAccept(privateChannel -> JDAWrappers.message(privateChannel, translate(guild, "reaction.not-configured")).submit());
			return PROCESSED;
		}
		
		JDAWrappers.delete(message).submit();
		return PROCESSED_DELETE;
	}
	
	@NotNull
	private ReactionHandlerResult handleReply(@NotNull GuildMessageReactionAddEvent event, @NotNull User user, @NotNull Message message){
		var guild = event.getGuild();
		
		try{
			return JDAWrappers.createTextChannel(guild, "reply-" + event.getMessageIdLong()).submit()
					.thenApply(forwardChannel -> {
						ofNullable(message.getTextChannel().getParent())
								.ifPresent(category -> JDAWrappers.edit(forwardChannel)
										.setParent(category)
										.sync(category)
										.submit());
						JDAWrappers.message(forwardChannel, translate(guild, "reaction.original-from", message.getAuthor().getAsMention())).submit()
								.thenCompose(sent -> JDAWrappers.message(forwardChannel, message).submit())
								.thenCompose(sent -> JDAWrappers.message(forwardChannel, translate(guild, "reaction.react-archive", user.getAsMention()))
										.addActionRow(new ReplyChannelDeleteButtonHandler().asComponent())
										.submit())
								.thenCompose(sent -> JDAWrappers.delete(message).submit());
						return PROCESSED_DELETE;
					})
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
