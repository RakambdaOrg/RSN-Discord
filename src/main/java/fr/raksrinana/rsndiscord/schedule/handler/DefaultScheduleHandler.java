package fr.raksrinana.rsndiscord.schedule.handler;

import fr.raksrinana.rsndiscord.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.settings.guild.schedule.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Optional.ofNullable;

@ScheduleHandler
public class DefaultScheduleHandler implements IScheduleHandler{
	@Override
	public boolean acceptTag(@NotNull ScheduleTag tag){
		return true;
	}
	
	@Override
	public boolean accept(@NotNull Guild guild, @NotNull ScheduleConfiguration reminder){
		return reminder.getUser().getUser().flatMap(user -> reminder.getChannel().flatMap(ChannelConfiguration::getChannel).map(channel -> {
			JDAWrappers.message(channel, translate(channel.getGuild(), "schedule.reminder-added", user.getAsMention(), reminder.getMessage())).submit();
			ofNullable(reminder.getReminderCountdownMessage())
					.flatMap(MessageConfiguration::getMessage)
					.ifPresent(message -> JDAWrappers.delete(message).submit());
			return true;
		})).orElse(false);
	}
	
	@Override
	public int getPriority(){
		return Integer.MAX_VALUE;
	}
}
