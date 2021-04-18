package fr.raksrinana.rsndiscord.schedule.handler;

import fr.raksrinana.rsndiscord.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.settings.guild.schedule.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.schedule.ScheduleTag.DELETE_CHANNEL;

@ScheduleHandler
public class DeleteChannelScheduleHandler implements IScheduleHandler{
	@Override
	public boolean acceptTag(@NotNull ScheduleTag tag){
		return Objects.equals(tag, DELETE_CHANNEL);
	}
	
	@Override
	public boolean accept(@NotNull ScheduleConfiguration reminder){
		reminder.getChannel().getChannel().map(channel -> JDAWrappers.delete(channel).submit());
		return true;
	}
	
	@Override
	public int getPriority(){
		return 50;
	}
}
