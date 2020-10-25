package fr.raksrinana.rsndiscord.modules.externaltodos.reaction;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.modules.reaction.config.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.modules.reaction.handler.IReactionHandler;
import fr.raksrinana.rsndiscord.modules.reaction.handler.ReactionHandler;
import fr.raksrinana.rsndiscord.modules.reaction.handler.ReactionHandlerResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CHECK_OK;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CROSS_NO;
import static fr.raksrinana.rsndiscord.utils.Utilities.MAIN_RAKSRINANA_ACCOUNT;
@ReactionHandler
@Slf4j
public class ExternalTodoReactionHandler implements IReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, ReactionTag.EXTERNAL_TODO);
	}
	
	@Override
	public ReactionHandlerResult accept(@NonNull GuildMessageReactionAddEvent event, @NonNull WaitingReactionMessageConfiguration reaction){
		if(event.getReactionEmote().isEmoji()){
			final var emote = BasicEmotes.getEmote(event.getReactionEmote().getEmoji());
			if(isValidEmote(emote)){
				try{
					return processTodoCompleted(event, emote, reaction);
				}
				catch(InterruptedException | ExecutionException | TimeoutException e){
					Utilities.reportException("Failed to handle reaction", e);
					Log.getLogger(event.getGuild()).error("Failed to handle reaction", e);
					return ReactionHandlerResult.FAIL;
				}
			}
		}
		return ReactionHandlerResult.PROCESSED;
	}
	
	protected boolean isValidEmote(@NonNull BasicEmotes emote){
		return emote == CHECK_OK || emote == CROSS_NO;
	}
	
	protected ReactionHandlerResult processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull BasicEmotes emote, @NonNull WaitingReactionMessageConfiguration todo) throws InterruptedException, ExecutionException, TimeoutException{
		return event.retrieveUser().submit()
				.thenApply(user -> todo.getMessage()
						.getMessage()
						.map(message -> {
							Actions.sendPrivateMessage(event.getGuild(), MAIN_RAKSRINANA_ACCOUNT, user.getAsMention() + " reacted " + emote.getValue() + " => " + message.getContentRaw(), null)
									.thenAccept(messageSent -> Actions.deleteMessage(message));
							return ReactionHandlerResult.PROCESSED_DELETE;
						}).orElse(ReactionHandlerResult.PROCESSED))
				.get(30, TimeUnit.SECONDS);
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
}
