package fr.raksrinana.rsndiscord.modules.anilist.schedule;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.anilist.query.MediaPagedQuery;
import fr.raksrinana.rsndiscord.modules.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.modules.schedule.config.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.modules.schedule.handler.IScheduleHandler;
import fr.raksrinana.rsndiscord.modules.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import java.util.Objects;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class AnilistReleaseScheduleHandler implements IScheduleHandler{
	public static final String MEDIA_ID_KEY = "mediaId";
	
	@Override
	public boolean acceptTag(@NonNull ScheduleTag tag){
		return Objects.equals(tag, ScheduleTag.ANILIST_AIRING_SCHEDULE);
	}
	
	@Override
	public boolean accept(@NonNull ScheduleConfiguration reminder){
		final var data = reminder.getData();
		if(data.containsKey(MEDIA_ID_KEY)){
			return reminder.getUser().getUser()
					.flatMap(user -> reminder.getChannel().getChannel()
							.flatMap(channel -> Optional.ofNullable(channel.getGuild().getMember(user))
									.map(member -> {
										try{
											return new MediaPagedQuery(Integer.parseInt(data.get(MEDIA_ID_KEY))).getResult(member).stream().findFirst().map(media -> {
												final var builder = new EmbedBuilder();
												media.fillEmbed(member.getGuild(), builder);
												Actions.sendMessage(channel, translate(channel.getGuild(), "schedule.reminder-added", user.getAsMention(), reminder.getMessage()), builder.build());
												Optional.ofNullable(reminder.getReminderCountdownMessage()).flatMap(MessageConfiguration::getMessage).ifPresent(Actions::deleteMessage);
												return true;
											}).orElse(false);
										}
										catch(Exception e){
											Log.getLogger(member.getGuild()).error("Failed to get media", e);
										}
										return false;
									})))
					.orElse(false);
		}
		return false;
	}
	
	@Override
	public int getPriority(){
		return 50;
	}
}
