package fr.raksrinana.rsndiscord.utils.reaction;

import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.Objects;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CHECK_OK;

public class TodosReactionHandler implements ReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, ReactionTag.TODO);
	}
	
	@Override
	public ReactionHandlerResult accept(@NonNull GuildMessageReactionAddEvent event, @NonNull WaitingReactionMessageConfiguration reaction){
		if(event.getReactionEmote().isEmoji()){
			final var emote = BasicEmotes.getEmote(event.getReactionEmote().getEmoji());
			if(isValidEmote(emote)){
				processTodoCompleted(event, reaction);
				return ReactionHandlerResult.PROCESSED_DELETE;
			}
		}
		return ReactionHandlerResult.PROCESSED;
	}
	
	protected boolean isValidEmote(@NonNull BasicEmotes emote){
		return emote == CHECK_OK;
	}
	
	@Override
	public int getPriority(){
		return 1000;
	}
	
	protected void processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull WaitingReactionMessageConfiguration todo){
		todo.getMessage().getMessage().ifPresent(message -> {
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
		});
	}
}
