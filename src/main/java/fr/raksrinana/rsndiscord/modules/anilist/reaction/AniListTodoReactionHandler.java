package fr.raksrinana.rsndiscord.modules.anilist.reaction;

import fr.raksrinana.rsndiscord.modules.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.modules.reaction.config.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.modules.reaction.handler.ReactionHandler;
import fr.raksrinana.rsndiscord.modules.reaction.handler.ReactionHandlerResult;
import fr.raksrinana.rsndiscord.modules.reaction.handler.TodoReactionHandler;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.requests.RestAction;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import static fr.raksrinana.rsndiscord.modules.reaction.ReactionTag.ANILIST_TODO;
import static fr.raksrinana.rsndiscord.utils.Utilities.MAIN_RAKSRINANA_ACCOUNT;

@ReactionHandler
public class AniListTodoReactionHandler extends TodoReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, ANILIST_TODO);
	}
	
	@Override
	protected ReactionHandlerResult processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull BasicEmotes emotes, @NonNull WaitingReactionMessageConfiguration todo) throws InterruptedException, ExecutionException, TimeoutException{
		event.retrieveUser().submit()
				.thenAccept(user -> todo.getMessage().getMessage()
						.ifPresent(message -> message.getEmbeds().forEach(embed -> sendEmbed(event, user, embed))));
		return super.processTodoCompleted(event, emotes, todo);
	}
	
	private void sendEmbed(GuildMessageReactionAddEvent event, User user, MessageEmbed embed){
		Optional.ofNullable(event.getJDA().getUserById(MAIN_RAKSRINANA_ACCOUNT))
				.map(User::openPrivateChannel)
				.map(RestAction::submit)
				.ifPresent(future -> future
						.thenAccept(privateChannel -> privateChannel.sendMessage(user.getAsMention() + " completed").embed(embed).submit()));
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
}
