package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.commands.ParticipationCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import static java.time.ZoneOffset.UTC;

public class ParticipationScheduledRunner implements ScheduledRunner{
	private final JDA jda;
	
	public ParticipationScheduledRunner(JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		final var day = LocalDate.now(UTC).minusDays(1);
		this.jda.getGuilds().forEach(guild -> {
			Log.getLogger(guild).info("Processing guild {}", guild);
			final var participationConfiguration = Settings.get(guild).getParticipationConfiguration();
			if(participationConfiguration.getReportedDays().contains(day)){
				Log.getLogger(guild).info("Day {} has already been reported", day);
			}
			else{
				participationConfiguration.getReportChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(reportChannel -> {
					participationConfiguration.getChatDay(day).ifPresent(chatParticipation -> ParticipationCommand.sendMessagesReport(15, day, chatParticipation, this.jda.getSelfUser(), reportChannel));
					participationConfiguration.getVoiceDay(day).ifPresent(voiceParticipation -> ParticipationCommand.sendVoiceReport(15, day, voiceParticipation, this.jda.getSelfUser(), reportChannel));
					participationConfiguration.getReportedDays().add(day);
				});
			}
		});
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "participation";
	}
	
	@Override
	public long getPeriod(){
		return 1;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
}
