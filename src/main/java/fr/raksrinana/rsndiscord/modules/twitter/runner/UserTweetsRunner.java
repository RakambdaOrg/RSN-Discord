package fr.raksrinana.rsndiscord.modules.twitter.runner;

import fr.raksrinana.rsndiscord.modules.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.modules.twitter.TwitterUtils;
import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import twitter4j.Status;
import java.util.concurrent.TimeUnit;
import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
public class UserTweetsRunner implements IScheduledRunner{
	private final JDA jda;
	
	public UserTweetsRunner(@NonNull JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		jda.getGuilds().forEach(guild -> {
			var conf = Settings.get(guild).getTwitterConfiguration();
			conf.getUsersChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(channel ->
					conf.getUserIds().forEach(userId -> conf.getLastUserTweet(userId)
							.map(lastTweet -> TwitterUtils.getUserLastTweets(userId, lastTweet))
							.orElseGet(() -> TwitterUtils.getUserLastTweets(userId)).stream()
							.sorted(comparing(Status::getCreatedAt))
							.forEach(tweet -> {
								channel.sendMessage(String.format("https://twitter.com/%s/status/%s", tweet.getUser().getName(), tweet.getId())).submit()
										.thenAccept(ScheduleUtils.deleteMessage(date -> date.plusHours(6)));
								conf.setLastUserTweet(userId, tweet.getId());
							})));
		});
	}
	
	@Override
	public long getDelay(){
		return 2;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Twitter user last tweets";
	}
	
	@Override
	public long getPeriod(){
		return 15;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
}
