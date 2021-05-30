package fr.raksrinana.rsndiscord.schedule.handler;

import fr.raksrinana.rsndiscord.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.settings.guild.schedule.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.Guild;
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
	public boolean accept(@NotNull Guild guild, @NotNull ScheduleConfiguration reminder){
		reminder.getChannel().flatMap(ChannelConfiguration::getChannel).map(channel -> JDAWrappers.delete(channel).submit());
		return true;
	}
	
	@Override
	public int getPriority(){
		return 50;
	}
}
