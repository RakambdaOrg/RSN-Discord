package fr.raksrinana.rsndiscord.modules.anilist.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import fr.raksrinana.rsndiscord.modules.anilist.data.airing.AiringSchedule;
import fr.raksrinana.rsndiscord.modules.schedule.config.ScheduleConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.Map;
import static fr.raksrinana.rsndiscord.modules.anilist.schedule.AniListReleaseScheduleHandler.MEDIA_ID_KEY;
import static fr.raksrinana.rsndiscord.modules.schedule.ScheduleTag.ANILIST_AIRING_SCHEDULE;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class AniListAiringScheduleConfiguration extends ScheduleConfiguration{
	public AniListAiringScheduleConfiguration(@NonNull User user, @NonNull TextChannel channel, @NonNull ZonedDateTime scheduleDate, @NonNull AiringSchedule schedule){
		super(user, channel, scheduleDate, MessageFormat.format("Episode {0} is airing", schedule.getEpisode()),
				ANILIST_AIRING_SCHEDULE, Map.of(MEDIA_ID_KEY, Integer.toString(schedule.getMedia().getId())));
	}
}
