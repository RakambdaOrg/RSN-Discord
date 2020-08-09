package fr.raksrinana.rsndiscord.utils.reaction;

import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CHECK_OK;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CROSS_NO;
import static fr.raksrinana.rsndiscord.utils.Utilities.RAKSRINANA_ACCOUNT;

@Slf4j
public class ExternalTodoReactionHandler implements ReactionHandler{
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
							Actions.sendPrivateMessage(event.getGuild(), RAKSRINANA_ACCOUNT, user.getAsMention() + " reacted " + emote.getValue() + " => " + message.getContentRaw(), null)
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
