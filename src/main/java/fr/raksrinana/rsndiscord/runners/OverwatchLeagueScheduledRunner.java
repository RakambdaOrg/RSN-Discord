package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.overwatch.OverwatchUtils;
import fr.raksrinana.rsndiscord.utils.overwatch.stage.match.OverwatchMatch;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import java.util.concurrent.TimeUnit;

public class OverwatchLeagueScheduledRunner implements ScheduledRunner{
	@Getter
	private final JDA jda;
	
	public OverwatchLeagueScheduledRunner(JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		OverwatchUtils.getData().ifPresent(ow -> this.getJda().getGuilds().forEach(guild -> Settings.get(guild).getOverwatchLeagueConfiguration().getNotificationChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(channel -> {
			final var notified = Settings.get(guild).getOverwatchLeagueConfiguration().getNotifiedMatches();
			ow.getData().getStages().stream().flatMap(s -> s.getMatches().stream()).filter(OverwatchMatch::hasEnded).filter(m -> !notified.contains(m.getId())).sorted().forEachOrdered(m -> {
				Log.getLogger(guild).info("Notifying match {} to {}", m, channel);
				Actions.sendMessage(channel, "", m.buildEmbed(this.getJda().getSelfUser()).build());
				Settings.get(guild).getOverwatchLeagueConfiguration().setNotifiedMatch(m.getId());
			});
		})));
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Overwatch league";
	}
	
	@Override
	public long getDelay(){
		return 3;
	}
	
	@Override
	public long getPeriod(){
		return 10;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
}
