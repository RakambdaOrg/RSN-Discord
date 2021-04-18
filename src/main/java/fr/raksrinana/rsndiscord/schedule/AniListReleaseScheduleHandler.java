package fr.raksrinana.rsndiscord.schedule;

import fr.raksrinana.rsndiscord.api.anilist.query.MediaPagedQuery;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.schedule.handler.IScheduleHandler;
import fr.raksrinana.rsndiscord.settings.guild.schedule.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.schedule.ScheduleTag.ANILIST_AIRING_SCHEDULE;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Optional.ofNullable;

public class AniListReleaseScheduleHandler implements IScheduleHandler{
	public static final String MEDIA_ID_KEY = "mediaId";
	
	@Override
	public boolean acceptTag(@NotNull ScheduleTag tag){
		return Objects.equals(tag, ANILIST_AIRING_SCHEDULE);
	}
	
	@Override
	public boolean accept(@NotNull ScheduleConfiguration reminder){
		var data = reminder.getData();
		if(data.containsKey(MEDIA_ID_KEY)){
			return reminder.getUser().getUser()
					.flatMap(user -> reminder.getChannel().getChannel()
							.flatMap(channel -> {
								var guild = channel.getGuild();
								return ofNullable(guild.getMember(user)).map(member -> {
									try{
										return sendNotification(reminder, data, user, channel, guild, member);
									}
									catch(Exception e){
										Log.getLogger(member.getGuild()).error("Failed to get media", e);
									}
									return false;
								});
							}))
					.orElse(false);
		}
		return false;
	}
	
	@NotNull
	private Boolean sendNotification(ScheduleConfiguration reminder, Map<String, String> data, User user, TextChannel channel, Guild guild, Member member) throws Exception{
		return new MediaPagedQuery(Integer.parseInt(data.get(MEDIA_ID_KEY))).getResult(member).stream().findFirst()
				.map(media -> {
					var builder = new EmbedBuilder();
					media.fillEmbed(member.getGuild(), builder);
					
					JDAWrappers.message(channel, translate(guild, "schedule.reminder-added", user.getAsMention(), reminder.getMessage()))
							.embed(builder.build())
							.submit()
							.thenAccept(message -> ofNullable(reminder.getReminderCountdownMessage())
									.flatMap(MessageConfiguration::getMessage)
									.ifPresent(message1 -> JDAWrappers.delete(message1).submit()));
					
					return true;
				}).orElse(false);
	}
	
	@Override
	public int getPriority(){
		return 50;
	}
}
