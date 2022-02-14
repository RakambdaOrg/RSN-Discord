package fr.raksrinana.rsndiscord.runner.impl;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import fr.raksrinana.rsndiscord.runner.api.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.api.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.impl.guild.rss.RSSConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import kong.unirest.core.Unirest;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static java.awt.Color.GREEN;
import static java.util.concurrent.TimeUnit.MINUTES;

@NoArgsConstructor
@ScheduledRunner
@Log4j2
public class RSSRunner implements IScheduledRunner{
	
	@Override
	public void executeGlobal(@NotNull JDA jda) throws Exception{
	}
	
	@Override
	public void executeGuild(@NotNull Guild guild) throws Exception{
		var rssConfiguration = Settings.get(guild).getRss();
		rssConfiguration.getChannel()
				.flatMap(ChannelConfiguration::getChannel)
				.ifPresentOrElse(
						channel -> {
							var feeds = rssConfiguration.getFeeds();
							log.info("Processing {} RSS feeds for {}", feeds.size(), guild);
							feeds.forEach(url -> processFeed(rssConfiguration, channel, url));
						},
						() -> log.warn("No RSS channel defined for {}", guild));
	}
	
	private void processFeed(@NotNull RSSConfiguration rssConfiguration, @NotNull TextChannel channel, @NotNull URL url){
		var key = url.toString();
		log.info("Processing feed {} ({})", url, rssConfiguration.getFeedInfo(key).getTitle());
		
		var feedInfo = rssConfiguration.getFeedInfo(key);
		var lastDate = feedInfo.getLastPublicationDate();
		getFeed(url).ifPresent(feed -> {
			var entries = feed.getEntries();
			var newEntries = entries.stream()
					.sorted(this::sortByDate)
					.filter(entry -> lastDate.map(date -> {
								var entryTimestamp = extractDate(entry)
										.map(Instant::toEpochMilli)
										.orElse(0L);
								return date < entryTimestamp;
							})
							.orElse(true))
					.toList();
			
			log.info("Found {} new entries in feed {}", newEntries.size(), key);
			
			newEntries.forEach(entry -> publish(channel, feed, entry));
			
			newEntries.stream()
					.flatMap(entry -> extractDate(entry)
							.map(Instant::toEpochMilli)
							.stream())
					.mapToLong(l -> l)
					.max()
					.ifPresent(feedInfo::setLastPublicationDate);
			
			Optional.ofNullable(feed.getTitle()).ifPresent(feedInfo::setTitle);
		});
	}
	
	@NotNull
	private Optional<Instant> extractDate(@NotNull SyndEntry entry){
		return Optional.ofNullable(entry.getPublishedDate())
				.or(() -> Optional.ofNullable(entry.getUpdatedDate()))
				.map(Date::toInstant);
	}
	
	@NotNull
	private Optional<SyndFeed> getFeed(@NotNull URL url){
		var response = Unirest.get(url.toString()).asBytes();
		if(!response.isSuccess()){
			return Optional.empty();
		}
		
		try{
			var reader = new XmlReader(new ByteArrayInputStream(response.getBody()));
			var feed = new SyndFeedInput().build(reader);
			return Optional.ofNullable(feed);
		}
		catch(IOException | FeedException e){
			log.error("Failed to parse RSS feed", e);
			return Optional.empty();
		}
	}
	
	private void publish(@NotNull TextChannel channel, @NotNull SyndFeed feed, @NotNull SyndEntry entry){
		var title = Optional.ofNullable(entry.getTitle())
				.map(tit -> tit.substring(0, Math.min(tit.length(), MessageEmbed.TITLE_MAX_LENGTH)))
				.orElse("Empty");
		
		var builder = new EmbedBuilder();
		builder.setColor(GREEN);
		builder.setTitle(title, entry.getLink());
		builder.setDescription("RSS: " + feed.getTitle());
		Optional.ofNullable(entry.getDescription())
				.map(this::getContent)
				.map(desc -> desc.substring(0, Math.min(desc.length(), MessageEmbed.VALUE_MAX_LENGTH)))
				.ifPresent(desc -> builder.addField("Content", desc, false));
		Optional.ofNullable(entry.getAuthor()).ifPresent(builder::setAuthor);
		var categories = entry.getCategories().stream()
				.map(SyndCategory::getName)
				.collect(Collectors.joining(", "));
		if(!categories.isBlank()){
			builder.addField("Category", categories, true);
		}
		extractDate(entry).ifPresent(builder::setTimestamp);
		
		JDAWrappers.message(channel, builder.build()).submit();
	}
	
	@Nullable
	private String getContent(@NotNull SyndContent content){
		var value = content.getValue();
		if(Objects.isNull(value)){
			return null;
		}
		
		if(Objects.nonNull(content.getType()) && content.getType().contains("html")){
			return Jsoup.parse(value).text();
		}
		return value;
	}
	
	private int sortByDate(@NotNull SyndEntry entry1, @NotNull SyndEntry entry2){
		var date1 = extractDate(entry1);
		var date2 = extractDate(entry2);
		
		if(date1.isPresent() && date2.isPresent()){
			return date1.get().compareTo(date2.get());
		}
		if(date1.isPresent()){
			return 1;
		}
		if(date2.isPresent()){
			return -1;
		}
		return 0;
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@Override
	public long getPeriod(){
		return 15;
	}
	
	@Override
	@NotNull
	public String getName(){
		return "rss";
	}
	
	@Override
	@NotNull
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
}
