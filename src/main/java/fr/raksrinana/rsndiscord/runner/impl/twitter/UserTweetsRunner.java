package fr.raksrinana.rsndiscord.runner.impl.twitter;

import io.github.redouane59.twitter.dto.tweet.Tweet;
import fr.raksrinana.rsndiscord.api.twitter.TwitterApi;
import fr.raksrinana.rsndiscord.runner.api.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.api.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;
import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
public class UserTweetsRunner implements IScheduledRunner{
	@Override
	public void executeGlobal(@NotNull JDA jda){
	}
	
	@Override
	public void executeGuild(@NotNull Guild guild) throws Exception{
		var conf = Settings.get(guild).getTwitterConfiguration();
		conf.getUsersChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(channel ->
				conf.getUserIds().forEach(userId -> conf.getLastUserTweet(userId)
						.map(lastTweet -> TwitterApi.getUserLastTweets(userId, lastTweet))
						.orElseGet(() -> TwitterApi.getUserLastTweets(userId))
						.getData().stream()
						.sorted(comparing(Tweet::getCreatedAt))
						.forEach(tweet -> {
							JDAWrappers.message(channel, TwitterApi.getUrl(tweet)).submit();
							conf.setLastUserTweet(userId, tweet.getId());
						})));
	}
	
	@Override
	public long getDelay(){
		return 2;
	}
	
	@NotNull
	@Override
	public String getName(){
		return "Twitter user last tweets";
	}
	
	@Override
	public long getPeriod(){
		return 15;
	}
	
	@NotNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
}
