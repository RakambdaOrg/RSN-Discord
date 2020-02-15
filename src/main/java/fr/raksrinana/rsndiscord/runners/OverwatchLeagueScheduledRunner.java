package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.OverwatchUtils;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import java.awt.Color;
import java.util.concurrent.TimeUnit;

public class OverwatchLeagueScheduledRunner implements ScheduledRunner{
	@Getter
	private final JDA jda;
	
	public OverwatchLeagueScheduledRunner(JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		final var weeksData = OverwatchUtils.getWeeksData();
		this.getJda().getGuilds().forEach(guild -> Settings.get(guild).getOverwatchLeagueConfiguration().getNotificationChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(channel -> {
			final var notified = Settings.get(guild).getOverwatchLeagueConfiguration().getNotifiedMatches();
			weeksData.stream().flatMap(weekData -> weekData.getEvents().stream()).flatMap(event -> event.getMatches().stream()).forEach(match -> {
				Log.getLogger(guild).info("Notifying match {} to {}", match, channel);
				final var embed = Utilities.buildEmbed(guild.getJDA().getSelfUser(), Color.GREEN, "", null);
				match.fillEmbed(embed);
				Actions.sendMessage(channel, "", embed.build());
				Settings.get(guild).getOverwatchLeagueConfiguration().setNotifiedMatch(match.getId());
			});
		}));
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
