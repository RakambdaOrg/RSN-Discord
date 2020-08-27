package fr.raksrinana.rsndiscord.utils.schedule;

import fr.raksrinana.rsndiscord.settings.guild.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.TextChannel;
import java.util.Objects;

public class UnbanUserScheduleHandler implements ScheduleHandler{
	public static final String USER_ID_KEY = "userId";
	
	@Override
	public boolean acceptTag(@NonNull ScheduleTag tag){
		return Objects.equals(tag, ScheduleTag.UNBAN_USER);
	}
	
	@Override
	public boolean accept(@NonNull ScheduleConfiguration reminder){
		reminder.getChannel().getChannel()
				.map(TextChannel::getGuild)
				.ifPresent(guild -> Actions.unban(guild, reminder.getData().get(USER_ID_KEY)));
		return true;
	}
	
	@Override
	public int getPriority(){
		return 50;
	}
}
