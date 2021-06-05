package fr.raksrinana.rsndiscord.reaction.handler;

import fr.raksrinana.rsndiscord.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.settings.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static fr.raksrinana.rsndiscord.reaction.ReactionTag.EXTERNAL_TODO;
import static fr.raksrinana.rsndiscord.reaction.handler.ReactionHandlerResult.*;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CHECK_OK;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CROSS_NO;
import static fr.raksrinana.rsndiscord.utils.Utilities.MAIN_RAKSRINANA_ACCOUNT;

@ReactionHandler
@Slf4j
public class ExternalTodoReactionHandler implements IReactionHandler{
	@Override
	public boolean acceptTag(@NotNull ReactionTag tag){
		return Objects.equals(tag, EXTERNAL_TODO);
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
		return emote == CHECK_OK || emote == CROSS_NO;
	}
	
	protected ReactionHandlerResult processTodoCompleted(@NotNull GuildMessageReactionAddEvent event, @NotNull BasicEmotes emote, @NotNull WaitingReactionMessageConfiguration todo) throws InterruptedException, ExecutionException, TimeoutException{
		var user = event.retrieveUser().submit().get(30, TimeUnit.SECONDS);
		
		return todo.getMessage().getMessage()
				.map(message -> {
					var messageContent = user.getAsMention() + " reacted " + emote.getValue() + " => " + message.getContentRaw();
					Optional.ofNullable(event.getJDA().getUserById(MAIN_RAKSRINANA_ACCOUNT))
							.ifPresent(target -> target.openPrivateChannel().submit()
									.thenAccept(privateChannel -> {
										JDAWrappers.message(privateChannel, messageContent).submit();
										JDAWrappers.delete(message).submit();
									}));
					return PROCESSED_DELETE;
				}).orElse(PROCESSED);
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
}
