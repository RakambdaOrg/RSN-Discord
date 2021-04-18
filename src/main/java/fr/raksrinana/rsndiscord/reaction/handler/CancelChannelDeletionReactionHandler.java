package fr.raksrinana.rsndiscord.reaction.handler;

import fr.raksrinana.rsndiscord.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.reaction.ReactionTag.SCHEDULED_DELETE_CHANNEL;
import static fr.raksrinana.rsndiscord.reaction.handler.ReactionHandlerResult.PROCESSED;
import static fr.raksrinana.rsndiscord.reaction.handler.ReactionHandlerResult.PROCESSED_DELETE;
import static fr.raksrinana.rsndiscord.schedule.ScheduleTag.DELETE_CHANNEL;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CROSS_NO;

@ReactionHandler
public class CancelChannelDeletionReactionHandler extends TodoReactionHandler{
	@Override
	public boolean acceptTag(@NotNull ReactionTag tag){
		return Objects.equals(tag, SCHEDULED_DELETE_CHANNEL);
	}
	
	@Override
	protected boolean isValidEmote(@NotNull BasicEmotes emote){
		return CROSS_NO == emote;
	}
	
	@Override
	@NotNull
	protected ReactionHandlerResult processTodoCompleted(@NotNull GuildMessageReactionAddEvent event, @NotNull BasicEmotes emote, @NotNull WaitingReactionMessageConfiguration todo){
		return todo.getMessage().getMessage().map(message -> {
			Settings.get(message.getGuild())
					.getSchedules().stream()
					.filter(schedule -> schedule.getTag() == DELETE_CHANNEL && schedule.getChannel()
							.getChannel()
							.map(channel -> Objects.equals(channel, message.getChannel()))
							.orElse(false))
					.forEach(schedule -> Settings.get(message.getGuild()).removeSchedule(schedule));
			JDAWrappers.delete(message).submit();
			return PROCESSED_DELETE;
		}).orElse(PROCESSED);
	}
	
	@Override
	public int getPriority(){
		return 990;
	}
}
