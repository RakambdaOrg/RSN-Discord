package fr.raksrinana.rsndiscord.settings.guild.schedule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import fr.raksrinana.rsndiscord.schedule.ScheduleTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class SimpleScheduleConfiguration extends ScheduleConfiguration{
	public SimpleScheduleConfiguration(@NotNull User user, @NotNull TextChannel channel, @NotNull ZonedDateTime scheduleDate, @NotNull String message){
		super(user, channel, scheduleDate, message);
	}
	
	public SimpleScheduleConfiguration(@NotNull User user, @NotNull TextChannel channel, @NotNull ZonedDateTime scheduleDate, @NotNull String message, @NotNull ScheduleTag tag){
		super(user, channel, scheduleDate, message, tag);
	}
}
