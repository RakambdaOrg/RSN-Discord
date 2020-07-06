package fr.raksrinana.rsndiscord.utils.reaction;

import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.Objects;
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
				return processTodoCompleted(event, emote, reaction);
			}
		}
		return ReactionHandlerResult.PROCESSED;
	}
	
	protected boolean isValidEmote(@NonNull BasicEmotes emote){
		return emote == CHECK_OK || emote == CROSS_NO;
	}
	
	protected ReactionHandlerResult processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull BasicEmotes emote, @NonNull WaitingReactionMessageConfiguration todo){
		return todo.getMessage().getMessage().map(message -> {
			Actions.sendPrivateMessage(event.getGuild(), RAKSRINANA_ACCOUNT, event.getMember().getAsMention() + " reacted " + emote.getValue() + " => " + message.getContentRaw(), null)
					.thenAccept(messageSent -> Actions.deleteMessage(message));
			return ReactionHandlerResult.PROCESSED_DELETE;
		}).orElse(ReactionHandlerResult.PROCESSED);
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
}
