package fr.rakambda.rsndiscord.spring.schedule.impl;

import fr.rakambda.rsndiscord.spring.api.twitter.TwitterService;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.schedule.WrappedTriggerTask;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelEntity;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.entity.TwitterEntity;
import fr.rakambda.rsndiscord.spring.storage.entity.TwitterType;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import fr.rakambda.rsndiscord.spring.storage.repository.TwitterRepository;
import io.github.redouane59.twitter.dto.tweet.Tweet;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.util.Comparator.comparing;

@Component
@Slf4j
public class UserTweetsRunner extends WrappedTriggerTask{
	private final TwitterRepository twitterRepository;
	private final ChannelRepository channelRepository;
	private final TwitterService twitterService;
	
	@Autowired
	public UserTweetsRunner(@NotNull JDA jda, TwitterRepository twitterRepository, ChannelRepository channelRepository, TwitterService twitterService){
		super(jda);
		this.twitterRepository = twitterRepository;
		this.channelRepository = channelRepository;
		this.twitterService = twitterService;
	}
	
	@Override
	@NotNull
	public String getId(){
		return "twitter.user";
	}
	
	@Override
	public void executeGlobal(@NotNull JDA jda){
		var channels = channelRepository.findAllByType(ChannelType.TWITTER).stream()
				.map(ChannelEntity::getChannelId)
				.map(jda::getTextChannelById)
				.filter(Objects::nonNull)
				.toList();
		if(channels.isEmpty()){
			log.info("No channels configured, skipping");
			return;
		}
		
		var twitterUsers = twitterRepository.findAllByType(TwitterType.USER).stream().collect(Collectors.groupingBy(TwitterEntity::getSearch));
		if(twitterUsers.isEmpty()){
			log.info("No users configured, skipping");
			return;
		}
		
		var processedEntities = twitterUsers.entrySet().stream()
				.flatMap(entry -> processUser(entry.getKey(), entry.getValue(), filterChannels(channels, entry.getValue())))
				.toList();
		
		twitterRepository.saveAll(processedEntities);
	}
	
	@NotNull
	private Collection<TextChannel> filterChannels(@NotNull List<TextChannel> channels, @NotNull List<TwitterEntity> entities){
		var guildIds = entities.stream().map(TwitterEntity::getGuildId).collect(Collectors.toSet());
		return channels.stream()
				.filter(c -> guildIds.contains(c.getGuild().getIdLong()))
				.toList();
	}
	
	@NotNull
	private Stream<TwitterEntity> processUser(@NotNull String username, @NotNull Collection<TwitterEntity> entities, @NotNull Collection<TextChannel> channels){
		try{
			log.info("Processing user {}", username);
			
			if(channels.isEmpty()){
				log.warn("No channels defined, skipping");
				return Stream.empty();
			}
			
			var userId = twitterService.getUserIdFromUsername(username);
			
			var lastTweetId = entities.stream().map(TwitterEntity::getLastTweetId).filter(Objects::nonNull).findFirst().orElse(null);
			var tweets = twitterService.getUserLastTweets(userId.getId(), lastTweetId).getData();
			
			if(tweets.isEmpty()){
				log.info("No new tweets");
				return Stream.empty();
			}
			
			log.info("Found {} new tweets", tweets.size());
			tweets.stream().sorted(comparing(Tweet::getCreatedAt))
					.forEach(tweet -> sendTweet(tweet, channels));
			
			var lastId = tweets.stream().max(comparing(Tweet::getCreatedAt)).map(Tweet::getId).orElse(null);
			entities.forEach(te -> te.setLastTweetId(lastId));
			
			return entities.stream();
		}
		catch(Exception e){
			log.error("Failed to process tweets for {}", username, e);
			return Stream.empty();
		}
	}
	
	private void sendTweet(@NotNull Tweet tweet, @NotNull Collection<TextChannel> channels){
		var message = twitterService.getUrl(tweet).replace("https://twitter.com/", "https://fxtwitter.com/");
		channels.forEach(c -> JDAWrappers.message(c, message).submit());
	}
	
	@Override
	public void executeGuild(@NotNull Guild guild){
	}
	
	@NotNull
	@Override
	public String getName(){
		return "Twitter user last tweets";
	}
	
	@Override
	public long getPeriod(){
		return 30;
	}
	
	@NotNull
	@Override
	public TemporalUnit getPeriodUnit(){
		return ChronoUnit.MINUTES;
	}
}
