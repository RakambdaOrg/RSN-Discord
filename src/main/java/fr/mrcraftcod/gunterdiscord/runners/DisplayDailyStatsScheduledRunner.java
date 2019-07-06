package fr.mrcraftcod.gunterdiscord.runners;

import fr.mrcraftcod.gunterdiscord.commands.EmotesCommand;
import fr.mrcraftcod.gunterdiscord.commands.TempParticipationCommand;
import fr.mrcraftcod.gunterdiscord.listeners.LogListener;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.UserConfiguration;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 14/07/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-07-14
 */
public class DisplayDailyStatsScheduledRunner implements ScheduledRunner{
	private final JDA jda;
	
	/**
	 * Constructor.
	 *
	 * @param jda The JDA object.
	 */
	public DisplayDailyStatsScheduledRunner(@Nonnull final JDA jda){
		getLogger(null).info("Creating daily stats runner");
		this.jda = jda;
	}
	
	@Override
	public void run(){
		getLogger(null).info("Starting daily stats runner");
		final var ytd = LocalDate.now().minusDays(1);
		final var lastWeek = LocalDate.now().minusWeeks(1).minusDays(LogListener.getDaysToRemove(LocalDate.now().getDayOfWeek()));
		for(final var guild : this.jda.getGuilds()){
			NewSettings.getConfiguration(guild).getParticipationConfiguration().getReportChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(reportChannel -> {
				getLogger(guild).debug("Processing stats for guild {}", guild);
				var sent = false;
				if(TempParticipationCommand.sendInfos(guild, ytd, this.jda.getSelfUser(), reportChannel)){
					sent = true;
					NewSettings.getConfiguration(guild).getParticipationConfiguration().removeUsers(ytd);
				}
				getLogger(guild).debug("Processing stats for guild {}", guild);
				if(EmotesCommand.sendInfos(guild, lastWeek, this.jda.getSelfUser(), reportChannel, 10)){
					sent = true;
					NewSettings.getConfiguration(guild).getParticipationConfiguration().removeEmotes(lastWeek);
				}
				if(sent){
					final var toPin = NewSettings.getConfiguration(guild).getParticipationConfiguration().getUsersPinned().stream().map(UserConfiguration::getUser).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
					if(!toPin.isEmpty())
					Actions.sendMessage(reportChannel, toPin.stream().map(User::getAsMention).collect(Collectors.joining("\n")));
				}
			});
		}
		getLogger(null).info("Daily stats runner done");
	}
	
	@Override
	public long getPeriod(){
		return 2;
	}
	
	@Nonnull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
}
