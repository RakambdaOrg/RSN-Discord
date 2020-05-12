package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.commands.MessageParticipationCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
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
		this.jda.getGuilds().stream().map(guild -> Settings.get(guild).getParticipationConfiguration()).forEach(participationConfiguration -> {
			if(!participationConfiguration.getReportedDays().contains(day)){
				participationConfiguration.getDay(day).ifPresent(messageParticipation -> participationConfiguration.getReportChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(reportChannel -> {
					MessageParticipationCommand.sendReport(25, day, messageParticipation, this.jda.getSelfUser(), reportChannel);
					participationConfiguration.getReportedDays().add(day);
				}));
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
