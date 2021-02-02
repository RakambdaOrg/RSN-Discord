package fr.raksrinana.rsndiscord.reaction.handler;

import fr.raksrinana.rsndiscord.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.settings.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import static fr.raksrinana.rsndiscord.reaction.ReactionTag.ANILIST_TODO;
import static fr.raksrinana.rsndiscord.utils.Utilities.MAIN_RAKSRINANA_ACCOUNT;

@ReactionHandler
public class AniListTodoReactionHandler extends TodoReactionHandler{
	@Override
	public boolean acceptTag(@NotNull ReactionTag tag){
		return Objects.equals(tag, ANILIST_TODO);
	}
	
	@Override
	@NotNull
	protected ReactionHandlerResult processTodoCompleted(@NotNull GuildMessageReactionAddEvent event, @NotNull BasicEmotes emotes, @NotNull WaitingReactionMessageConfiguration todo) throws InterruptedException, ExecutionException, TimeoutException{
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
