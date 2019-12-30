package fr.raksrinana.rsndiscord.utils.reaction;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.TodoConfiguration;
import fr.raksrinana.rsndiscord.settings.types.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.Objects;
import java.util.Optional;

public class TodosReactionHandler implements ReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, ReactionTag.ACCEPTED_QUESTION);
	}
	
	@Override
	public ReactionHandlerResult accept(@NonNull GuildMessageReactionAddEvent event, @NonNull WaitingReactionMessageConfiguration reaction){
		if(event.getReactionEmote().isEmoji()){
			final var emote = BasicEmotes.getEmote(event.getReactionEmote().getEmoji());
			return Settings.get(event.getGuild()).getTodos().stream().filter(todo -> Objects.equals(todo.getMessage().getChannel().getChannelId(), event.getChannel().getIdLong())).filter(todo -> Objects.equals(todo.getMessage().getMessageId(), event.getMessageIdLong())).findFirst().map(todo -> {
				if(emote == BasicEmotes.CHECK_OK){
					processTodoCompleted(event, todo);
				}
				return ReactionHandlerResult.PROCESSED;
			}).orElse(ReactionHandlerResult.PROCESSED);
		}
		return ReactionHandlerResult.PROCESSED;
	}
	
	@Override
	public int getPriority(){
		return 1000;
	}
	
	private void processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull TodoConfiguration todo){
		todo.getMessage().getMessage().ifPresent(message -> {
			if(Objects.equals(todo.getMessage().getChannel().getChannelId(), Settings.get(event.getGuild()).getAniListConfiguration().getThaChannel().map(ChannelConfiguration::getChannelId).orElse(null))){
				Optional.ofNullable(event.getJDA().getUserById(Utilities.RAKSRINANA_ACCOUNT)).map(User::openPrivateChannel).ifPresent(user -> user.queue(privateChannel -> message.getEmbeds().forEach(embed -> Actions.sendPrivateMessage(event.getGuild(), privateChannel, event.getMember().getUser().getAsMention() + " completed", embed))));
			}
			if(todo.isDeleteOnDone()){
				Actions.deleteMessage(message);
			}
			else{
				Actions.editMessage(message, BasicEmotes.OK_HAND.getValue() + " __**DONE**__:  " + message.getContentRaw());
				Actions.clearReactions(message);
				message.clearReactions().queue();
				if(message.isPinned()){
					Actions.unpin(message);
				}
			}
			Settings.get(event.getGuild()).removeTodo(todo);
		});
	}
}
