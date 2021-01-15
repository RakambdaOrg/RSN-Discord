package fr.raksrinana.rsndiscord.reaction.handler;

import fr.raksrinana.rsndiscord.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.reaction.ReactionTag.SCHEDULED_DELETE_CHANNEL;
import static fr.raksrinana.rsndiscord.reaction.handler.ReactionHandlerResult.PROCESSED;
import static fr.raksrinana.rsndiscord.reaction.handler.ReactionHandlerResult.PROCESSED_DELETE;
import static fr.raksrinana.rsndiscord.schedule.ScheduleTag.DELETE_CHANNEL;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CROSS_NO;

@ReactionHandler
public class CancelChannelDeletionReactionHandler extends TodoReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, SCHEDULED_DELETE_CHANNEL);
	}
	
	@Override
	protected boolean isValidEmote(@NonNull BasicEmotes emote){
		return CROSS_NO == emote;
	}
	
	protected ReactionHandlerResult processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull BasicEmotes emote, @NonNull WaitingReactionMessageConfiguration todo){
		return todo.getMessage().getMessage().map(message -> {
			Settings.get(message.getGuild())
					.getSchedules().stream()
					.filter(schedule -> schedule.getTag() == DELETE_CHANNEL && schedule.getChannel()
							.getChannel()
							.map(channel -> Objects.equals(channel, message.getChannel()))
							.orElse(false))
					.forEach(schedule -> Settings.get(message.getGuild()).removeSchedule(schedule));
			message.delete().submit();
			return PROCESSED_DELETE;
		}).orElse(PROCESSED);
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
}
