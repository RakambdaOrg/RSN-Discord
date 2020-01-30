package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.RequestException;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.eslgaming.ESLUtils;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.generic.ESLRegion;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.Match;
import fr.raksrinana.rsndiscord.utils.eslgaming.requests.MatchPerDayGetRequest;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import java.awt.Color;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Rainbow6ProLeagueScheduledRunner implements ScheduledRunner{
	@Getter
	private final JDA jda;
	
	public Rainbow6ProLeagueScheduledRunner(JDA jda){
		this.jda = jda;
		Log.getLogger(null).info("Creating R6 PL runner");
	}
	
	@Override
	public void run(){
		Log.getLogger(null).info("Starting R6 PL runner");
		final var matches = Arrays.stream(ESLRegion.values()).map(MatchPerDayGetRequest::new).map(query -> {
			try{
				return ESLUtils.getQuery(query);
			}
			catch(RequestException e){
				Log.getLogger(null).error("Failed to get R6 PL infos");
			}
			return null;
		}).filter(Objects::nonNull).flatMap(requestResult -> requestResult.getItems().stream()).flatMap(matchDay -> matchDay.getMatches().stream()).filter(Match::isCompleted).collect(Collectors.toList());
		this.getJda().getGuilds().forEach(guild -> {
			Settings.get(guild).getRainbow6ProLeagueConfiguration().getNotificationChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(channel -> {
				final var notified = Settings.get(guild).getRainbow6ProLeagueConfiguration().getNotifiedMatches();
				matches.stream().filter(match -> !notified.contains(match.getUid())).sorted().forEachOrdered(match -> {
					Log.getLogger(guild).info("Notifying match {} to {}", match, channel);
					final var embed = Utilities.buildEmbed(this.getJda().getSelfUser(), Color.GREEN, null, null);
					match.buildEmbed(embed);
					Actions.sendMessage(channel, "", embed.build());
					Settings.get(guild).getRainbow6ProLeagueConfiguration().setNotifiedMatch(match.getUid());
				});
			});
		});
		Log.getLogger(null).info("R6 PL runner done");
	}
	
	@Override
	public long getDelay(){
		return 4;
	}
	
	@Override
	public long getPeriod(){
		return 30;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
}
