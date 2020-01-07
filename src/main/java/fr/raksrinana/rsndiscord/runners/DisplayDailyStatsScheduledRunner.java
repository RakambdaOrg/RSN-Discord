package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.commands.EmotesCommand;
import fr.raksrinana.rsndiscord.commands.TempParticipationCommand;
import fr.raksrinana.rsndiscord.listeners.LogListener;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DisplayDailyStatsScheduledRunner implements ScheduledRunner{
	@Getter
	private final JDA jda;
	
	public DisplayDailyStatsScheduledRunner(JDA jda){
		this.jda = jda;
		Log.getLogger(null).info("Creating daily stats runner");
	}
	
	@Override
	public void run(){
		Log.getLogger(null).info("Starting daily stats runner");
		final var ytd = LocalDate.now().minusDays(1);
		final var lastWeek = LocalDate.now().minusWeeks(1).minusDays(LogListener.getDaysToRemove(LocalDate.now().getDayOfWeek()));
		for(final var guild : this.getJda().getGuilds()){
			Settings.get(guild).getParticipationConfig().getReportChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(reportChannel -> {
				Log.getLogger(guild).debug("Processing stats for guild {}", guild);
				var sent = false;
				if(TempParticipationCommand.sendInfos(guild, ytd, this.getJda().getSelfUser(), reportChannel, 15)){
					sent = true;
					Settings.get(guild).getParticipationConfig().removeUsers(ytd);
					Settings.get(guild).getParticipationConfig().removeUsersBefore(ytd);
				}
				Log.getLogger(guild).debug("Processing stats for guild {}", guild);
				if(EmotesCommand.sendInfos(guild, lastWeek, this.getJda().getSelfUser(), reportChannel, 10)){
					sent = true;
					Settings.get(guild).getParticipationConfig().removeEmotes(lastWeek);
					Settings.get(guild).getParticipationConfig().removeEmotesBefore(lastWeek);
				}
				if(sent){
					final var toPin = Settings.get(guild).getParticipationConfig().getUsersPinned().stream().map(UserConfiguration::getUser).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
					if(!toPin.isEmpty()){
						Actions.sendMessage(reportChannel, toPin.stream().map(User::getAsMention).collect(Collectors.joining("\n")), null);
					}
				}
			});
		}
		Log.getLogger(null).info("Daily stats runner done");
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@Override
	public long getPeriod(){
		return 2;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
}
