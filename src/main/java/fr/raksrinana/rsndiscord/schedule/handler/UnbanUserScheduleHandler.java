package fr.raksrinana.rsndiscord.schedule.handler;

import fr.raksrinana.rsndiscord.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.settings.guild.schedule.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.schedule.ScheduleTag.UNBAN_USER;

@ScheduleHandler
public class UnbanUserScheduleHandler implements IScheduleHandler{
	public static final String USER_ID_KEY = "userId";
	
	@Override
	public boolean acceptTag(@NotNull ScheduleTag tag){
		return Objects.equals(tag, UNBAN_USER);
	}
	
	@Override
	public boolean accept(@NotNull ScheduleConfiguration reminder){
		reminder.getChannel().getChannel()
				.map(TextChannel::getGuild)
				.ifPresent(guild -> JDAWrappers.unban(guild, reminder.getData().get(USER_ID_KEY)).submit());
		return true;
	}
	
	@Override
	public int getPriority(){
		return 50;
	}
}
