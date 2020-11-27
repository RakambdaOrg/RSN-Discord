package fr.raksrinana.rsndiscord.modules.twitter.runner;

import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.modules.twitter.TwitterUtils;
import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import twitter4j.Status;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.SECONDS;

@ScheduledRunner
public class SearchTweetsRunner implements IScheduledRunner{
	private final JDA jda;
	
	public SearchTweetsRunner(@NonNull JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		jda.getGuilds().forEach(guild -> {
			var conf = Settings.get(guild).getTwitterConfiguration();
			conf.getSearchChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(channel ->
					conf.getSearches().forEach(search -> conf.getLastSearchTweet(search)
							.map(lastTweet -> TwitterUtils.searchLastTweets(search, lastTweet))
							.orElseGet(() -> TwitterUtils.searchLastTweets(search)).stream()
							.sorted(comparing(Status::getCreatedAt))
							.forEach(tweet -> {
								channel.sendMessage(String.format("https://twitter.com/%s/status/%s",
										URLEncoder.encode(tweet.getUser().getScreenName(), UTF_8),
										tweet.getId())
								).submit();
								conf.setLastSearchTweet(search, tweet.getId());
							})));
		});
	}
	
	@Override
	public long getDelay(){
		return 30;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Twitter search last tweets";
	}
	
	@Override
	public long getPeriod(){
		return 30;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return SECONDS;
	}
}
