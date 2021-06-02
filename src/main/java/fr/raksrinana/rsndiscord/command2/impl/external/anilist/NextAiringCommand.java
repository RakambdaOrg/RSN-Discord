package fr.raksrinana.rsndiscord.command2.impl.external.anilist;

import fr.raksrinana.rsndiscord.api.anilist.data.airing.AiringSchedule;
import fr.raksrinana.rsndiscord.api.anilist.query.AiringSchedulePagedQuery;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.settings.guild.anilist.AniListAiringScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import static fr.raksrinana.rsndiscord.command.CommandResult.FAILED;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS_NO_MESSAGE;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.addScheduleAndNotify;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Comparator.comparingInt;

public class NextAiringCommand extends SubCommand{
	private static final String MEDIA_OPTION_ID = "media";
	
	@Override
	@NotNull
	public String getId(){
		return "next-airing";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Get the next airing date of a media";
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		var mediaId = getOptionAsInt(event.getOption(MEDIA_OPTION_ID)).orElseThrow();
		
		var channel = event.getTextChannel();
		
		try{
			var now = ZonedDateTime.now();
			var schedules = new AiringSchedulePagedQuery(mediaId).getResult(event.getMember());
			
			schedules.stream().filter(schedule -> now.isBefore(schedule.getAiringAt()))
					.min(comparingInt(AiringSchedule::getTimeUntilAiring))
					.ifPresentOrElse(schedule -> {
						var builder = new EmbedBuilder();
						schedule.fillEmbed(guild, builder);
						
						var airingScheduleConfiguration = new AniListAiringScheduleConfiguration(
								event.getUser(), channel, schedule.getDate(), schedule);
						addScheduleAndNotify(airingScheduleConfiguration, channel);
					}, () -> JDAWrappers.message(channel, translate(guild, "anilist.airing-schedule-not-found")).submit()
							.thenAccept(deleteMessage(date -> date.plusMinutes(5))));
		}
		catch(Exception e){
			Log.getLogger(event.getGuild()).error("Failed to get airing schedule", e);
			return FAILED;
		}
		return SUCCESS_NO_MESSAGE;
	}
}
