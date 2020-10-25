package fr.raksrinana.rsndiscord.modules.anilist.reaction;

import fr.raksrinana.rsndiscord.modules.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.modules.reaction.config.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.modules.reaction.handler.ReactionHandler;
import fr.raksrinana.rsndiscord.modules.reaction.handler.ReactionHandlerResult;
import fr.raksrinana.rsndiscord.modules.reaction.handler.TodoReactionHandler;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@ReactionHandler
public class AnilistTodoReactionHandler extends TodoReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, ReactionTag.ANILIST_TODO);
	}
	
	@Override
	protected ReactionHandlerResult processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull BasicEmotes emotes, @NonNull WaitingReactionMessageConfiguration todo) throws InterruptedException, ExecutionException, TimeoutException{
		event.retrieveUser().submit().thenAccept(user -> todo.getMessage()
				.getMessage()
				.ifPresent(message -> message.getEmbeds()
						.forEach(embed -> Actions.sendPrivateMessage(message.getGuild(), Utilities.MAIN_RAKSRINANA_ACCOUNT, user.getAsMention() + " completed", embed))));
		return super.processTodoCompleted(event, emotes, todo);
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
}
