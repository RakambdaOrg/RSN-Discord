package fr.raksrinana.rsndiscord.schedule.handler;

import fr.raksrinana.rsndiscord.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.settings.guild.schedule.ScheduleConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.TextChannel;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.schedule.ScheduleTag.UNBAN_USER;

@ScheduleHandler
public class UnbanUserScheduleHandler implements IScheduleHandler{
	public static final String USER_ID_KEY = "userId";
	
	@Override
	public boolean acceptTag(@NonNull ScheduleTag tag){
		return Objects.equals(tag, UNBAN_USER);
	}
	
	@Override
	public boolean accept(@NonNull ScheduleConfiguration reminder){
		reminder.getChannel().getChannel()
				.map(TextChannel::getGuild)
				.ifPresent(guild -> guild.unban(reminder.getData().get(USER_ID_KEY)).submit());
		return true;
	}
	
	@Override
	public int getPriority(){
		return 50;
	}
}
