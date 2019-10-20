package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.settings.NewSettings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.overwatch.OverwatchUtils;
import fr.raksrinana.rsndiscord.utils.overwatch.stage.match.OverwatchMatch;
import net.dv8tion.jda.api.JDA;
import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 14/07/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-07-14
 */
public class OverwatchLeagueScheduledRunner implements ScheduledRunner{
	private final JDA jda;
	
	/**
	 * Constructor.
	 *
	 * @param jda The JDA.
	 */
	public OverwatchLeagueScheduledRunner(final JDA jda){
		Log.getLogger(null).info("Creating Overwatch league runner");
		this.jda = jda;
	}
	
	@Override
	public void run(){
		Log.getLogger(null).info("Starting Overwatch league runner");
		OverwatchUtils.getLastResponse().ifPresent(ow -> this.jda.getGuilds().forEach(guild -> NewSettings.getConfiguration(guild).getOverwatchLeagueConfiguration().getNotificationChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(channel -> {
			final var notified = NewSettings.getConfiguration(guild).getOverwatchLeagueConfiguration().getNotifiedMatches();
			ow.getData().getStages().stream().flatMap(s -> s.getMatches().stream()).filter(OverwatchMatch::hasEnded).filter(m -> !notified.contains(m.getId())).sorted().forEachOrdered(m -> {
				Log.getLogger(guild).info("Notifying match {} to {}", m, channel);
				Actions.sendMessage(channel, m.buildEmbed(this.jda.getSelfUser()).build());
				NewSettings.getConfiguration(guild).getOverwatchLeagueConfiguration().setNotifiedMatch(m.getId());
			});
		})));
		Log.getLogger(null).info("Overwatch league runner done");
	}
	
	@Override
	public long getPeriod(){
		return 10;
	}
	
	@Nonnull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
	
	@Override
	public long getDelay(){
		return 1;
	}
}
