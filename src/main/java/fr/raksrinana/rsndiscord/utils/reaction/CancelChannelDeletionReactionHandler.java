package fr.raksrinana.rsndiscord.utils.reaction;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleTag;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import java.util.Objects;

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
			Settings.get(message.getGuild()).getSchedules().stream().filter(schedule -> schedule.getTag() == ScheduleTag.DELETE_CHANNEL && schedule.getChannel().getChannel().map(chan -> Objects.equals(chan, message.getChannel())).orElse(false)).forEach(schedule -> Settings.get(message.getGuild()).removeSchedule(schedule));
			Actions.deleteMessage(message);
			return ReactionHandlerResult.PROCESSED_DELETE;
		}).orElse(ReactionHandlerResult.PROCESSED);
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
}
