package fr.raksrinana.rsndiscord.utils.reminder;

import fr.raksrinana.rsndiscord.settings.guild.ReminderConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.anilist.queries.MediaPagedQuery;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Optional;

public class AnilistReleaseReminderHandler implements ReminderHandler{
	public static final String MEDIA_ID_KEY = "mediaId";
	
	@Override
	public boolean acceptTag(@NonNull ReminderTag tag){
		return Objects.equals(tag, ReminderTag.ANILIST_AIRING_SCHEDULE);
	}
	
	@Override
	public boolean accept(@NonNull ReminderConfiguration reminder){
		final var data = reminder.getData();
		if(data.containsKey(MEDIA_ID_KEY)){
			return reminder.getUser().getUser().flatMap(user -> reminder.getChannel().getChannel().flatMap(channel -> Optional.ofNullable(channel.getGuild().getMember(user)).map(member -> {
				try{
					return new MediaPagedQuery(Integer.parseInt(data.get(MEDIA_ID_KEY))).getResult(member).stream().findFirst().map(media -> {
						final var builder = new EmbedBuilder();
						media.fillEmbed(builder);
						Actions.sendMessage(channel, MessageFormat.format("Reminder for {0}: {1}", user.getAsMention(), reminder.getMessage()), builder.build());
						Optional.ofNullable(reminder.getReminderCountdownMessage()).flatMap(MessageConfiguration::getMessage).ifPresent(Actions::deleteMessage);
						return true;
					}).orElse(false);
				}
				catch(Exception e){
					Log.getLogger(member.getGuild()).error("Failed to get media", e);
				}
				return false;
			}))).orElse(false);
		}
		return false;
	}
	
	@Override
	public int getPriority(){
		return 50;
	}
}
