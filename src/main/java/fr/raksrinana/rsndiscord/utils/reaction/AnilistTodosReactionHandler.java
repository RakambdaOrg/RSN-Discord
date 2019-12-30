package fr.raksrinana.rsndiscord.utils.reaction;

import fr.raksrinana.rsndiscord.settings.types.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.Objects;
import java.util.Optional;

public class AnilistTodosReactionHandler extends TodosReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, ReactionTag.ANILIST_TODO);
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
	
	@Override
	protected void processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull WaitingReactionMessageConfiguration todo){
		todo.getMessage().getMessage().ifPresent(message -> Optional.ofNullable(event.getJDA().getUserById(Utilities.RAKSRINANA_ACCOUNT)).map(User::openPrivateChannel).ifPresent(user -> user.queue(privateChannel -> message.getEmbeds().forEach(embed -> Actions.sendPrivateMessage(event.getGuild(), privateChannel, event.getMember().getUser().getAsMention() + " completed", embed)))));
		super.processTodoCompleted(event, todo);
	}
}
