package fr.mrcraftcod.gunterdiscord.runners;

import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.OverwatchUtils;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match.OverwatchMatch;
import net.dv8tion.jda.api.JDA;
import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

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
	 * @param jda
	 */
	public OverwatchLeagueScheduledRunner(JDA jda){
		getLogger(null).info("Creating overwatch league runner");
		this.jda = jda;
	}
	
	@Override
	public void run(){
		OverwatchUtils.getLastResponse().ifPresent(ow -> this.jda.getGuilds().forEach(guild -> {
			NewSettings.getConfiguration(guild).getOverwatchLeagueConfiguration().getNotificationChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(channel -> {
				final var notified = NewSettings.getConfiguration(guild).getOverwatchLeagueConfiguration().getNotifiedMatches();
				ow.getData().getStages().stream().flatMap(s -> s.getMatches().stream()).filter(OverwatchMatch::hasEnded).filter(m -> !notified.contains(m.getId())).sorted().forEachOrdered(m -> {
					Log.getLogger(guild).info("Notifying match {} to {}", m, channel);
					Actions.sendMessage(channel, m.buildEmbed(jda.getSelfUser()).build());
					NewSettings.getConfiguration(guild).getOverwatchLeagueConfiguration().setNotifiedMatch(m.getId());
				});
			});
		}));
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
