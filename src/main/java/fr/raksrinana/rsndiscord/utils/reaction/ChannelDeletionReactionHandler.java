package fr.raksrinana.rsndiscord.utils.reaction;

import fr.raksrinana.rsndiscord.commands.schedule.delete.ChannelCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.CategoryConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Objects;

public class ChannelDeletionReactionHandler extends TodosReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, ReactionTag.DELETE_CHANNEL);
	}
	
	@Override
	protected boolean isValidEmote(@NonNull BasicEmotes emote){
		return BasicEmotes.CROSS_NO == emote;
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
	
	protected ReactionHandlerResult processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull BasicEmotes emote, @NonNull WaitingReactionMessageConfiguration todo){
		return todo.getMessage().getMessage().map(message -> {
			Settings.get(event.getGuild()).getArchiveCategory().flatMap(CategoryConfiguration::getCategory).ifPresentOrElse(archiveCategory -> {
				Actions.setCategoryAndSync(message.getTextChannel(), archiveCategory).thenAccept(future -> Actions.sendMessage(message.getTextChannel(), MessageFormat.format("{0} archived this channel.", event.getMember().getAsMention()), null));
				ChannelCommand.scheduleDeletion(LocalDateTime.now().plusDays(4), message.getTextChannel(), event.getUser());
			}, () -> Actions.deleteChannel(message.getTextChannel()));
			return ReactionHandlerResult.PROCESSED_DELETE;
		}).orElse(ReactionHandlerResult.PROCESSED);
	}
}