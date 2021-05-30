package fr.raksrinana.rsndiscord.runner;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.command2.impl.moderation.ParticipationCommand.sendMessagesReport;
import static fr.raksrinana.rsndiscord.command2.impl.moderation.ParticipationCommand.sendVoiceReport;
import static java.time.ZoneOffset.UTC;

@ScheduledRunner
public class ParticipationScheduledRunner implements IScheduledRunner{
	private final JDA jda;
	
	public ParticipationScheduledRunner(@NotNull JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		var day = LocalDate.now(UTC).minusDays(1);
		jda.getGuilds().forEach(guild -> {
			Log.getLogger(guild).info("Processing guild {}", guild);
			var participationConfiguration = Settings.get(guild).getParticipationConfiguration();
			if(participationConfiguration.getReportedDays().contains(day)){
				Log.getLogger(guild).info("Day {} has already been reported", day);
			}
			else{
				participationConfiguration.getReportChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(reportChannel -> {
					participationConfiguration.getChatDay(day)
							.ifPresent(chatParticipation -> sendMessagesReport(guild, 15, day, chatParticipation, embed -> JDAWrappers.message(reportChannel, embed).submit()));
					participationConfiguration.getVoiceDay(day)
							.ifPresent(voiceParticipation -> sendVoiceReport(guild, 15, day, voiceParticipation, embed -> JDAWrappers.message(reportChannel, embed).submit()));
					participationConfiguration.getReportedDays().add(day);
				});
			}
		});
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@NotNull
	@Override
	public String getName(){
		return "Participation";
	}
	
	@Override
	public long getPeriod(){
		return 1;
	}
	
	@NotNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
}
