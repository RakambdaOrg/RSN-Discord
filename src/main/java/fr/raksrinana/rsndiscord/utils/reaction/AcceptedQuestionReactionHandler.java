package fr.raksrinana.rsndiscord.utils.reaction;

import fr.raksrinana.rsndiscord.settings.types.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.Objects;

public class AcceptedQuestionReactionHandler implements ReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, ReactionTag.ACCEPTED_QUESTION);
	}
	
	@Override
	public ReactionHandlerResult accept(@NonNull GuildMessageReactionAddEvent event, @NonNull WaitingReactionMessageConfiguration reaction){
		if(event.getReactionEmote().isEmoji()){
			final var emote = BasicEmotes.getEmote(event.getReactionEmote().getEmoji());
			if(emote == BasicEmotes.CHECK_OK){
				Utilities.getMessageById(event.getChannel(), event.getMessageIdLong()).thenAccept(Actions::deleteMessage);
				return ReactionHandlerResult.PROCESSED_DELETE;
			}
		}
		return ReactionHandlerResult.PROCESSED;
	}
	
	@Override
	public int getPriority(){
		return 1000;
	}
}
