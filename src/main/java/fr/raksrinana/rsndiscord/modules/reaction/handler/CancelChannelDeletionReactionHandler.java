package fr.raksrinana.rsndiscord.modules.reaction.handler;

import fr.raksrinana.rsndiscord.modules.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.modules.reaction.config.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.modules.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.Objects;

@ReactionHandler
public class CancelChannelDeletionReactionHandler extends TodoReactionHandler{
	@Override
	public boolean acceptTag(@NonNull ReactionTag tag){
		return Objects.equals(tag, ReactionTag.SCHEDULED_DELETE_CHANNEL);
	}
	
	@Override
	protected boolean isValidEmote(@NonNull BasicEmotes emote){
		return BasicEmotes.CROSS_NO == emote;
	}
	
	protected ReactionHandlerResult processTodoCompleted(@NonNull GuildMessageReactionAddEvent event, @NonNull BasicEmotes emote, @NonNull WaitingReactionMessageConfiguration todo){
		return todo.getMessage().getMessage().map(message -> {
			Settings.get(message.getGuild())
					.getSchedules().stream()
					.filter(schedule -> schedule.getTag() == ScheduleTag.DELETE_CHANNEL && schedule.getChannel()
							.getChannel()
							.map(channel -> Objects.equals(channel, message.getChannel()))
							.orElse(false))
					.forEach(schedule -> Settings.get(message.getGuild()).removeSchedule(schedule));
			Actions.deleteMessage(message);
			return ReactionHandlerResult.PROCESSED_DELETE;
		}).orElse(ReactionHandlerResult.PROCESSED);
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
}
