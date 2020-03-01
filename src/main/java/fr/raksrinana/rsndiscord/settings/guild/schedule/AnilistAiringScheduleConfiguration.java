package fr.raksrinana.rsndiscord.settings.guild.schedule;

import fr.raksrinana.rsndiscord.settings.guild.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.anilist.airing.AiringSchedule;
import fr.raksrinana.rsndiscord.utils.schedule.AnilistReleaseScheduleHandler;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleTag;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Map;

public class AnilistAiringScheduleConfiguration extends ScheduleConfiguration{
	public AnilistAiringScheduleConfiguration(@NonNull User user, @NonNull TextChannel channel, @NonNull LocalDateTime scheduleDate, @NonNull AiringSchedule schedule){
		super(user, channel, scheduleDate, MessageFormat.format("Episode {0} is airing", schedule.getEpisode()), ScheduleTag.ANILIST_AIRING_SCHEDULE, Map.of(AnilistReleaseScheduleHandler.MEDIA_ID_KEY, Integer.toString(schedule.getMedia().getId())));
	}
}
