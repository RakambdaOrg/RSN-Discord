package fr.rakambda.rsndiscord.spring.schedule.impl;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import fr.rakambda.rsndiscord.spring.api.HttpUtils;
import fr.rakambda.rsndiscord.spring.api.exceptions.RequestFailedException;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.schedule.WrappedTriggerTask;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelEntity;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.entity.GuildEntity;
import fr.rakambda.rsndiscord.spring.storage.entity.RssEntity;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import fr.rakambda.rsndiscord.spring.storage.repository.RssRepository;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static java.awt.Color.GREEN;

@Component
@Slf4j
public class RSSRunner extends WrappedTriggerTask{
	private final RssRepository rssRepository;
	private final ChannelRepository channelRepository;
	private final WebClient client;
	
	@Autowired
	public RSSRunner(@NotNull JDA jda, RssRepository rssRepository, ChannelRepository channelRepository){
		super(jda);
		this.rssRepository = rssRepository;
		this.channelRepository = channelRepository;
		
		client = WebClient.builder()
				.codecs(b -> b.defaultCodecs().maxInMemorySize(10_485_760))
				.build();
	}
	
	@Override
	@NotNull
	public String getId(){
		return "rss";
	}
	
	@Override
	public void executeGlobal(@NotNull JDA jda){
		var channels = channelRepository.findAllByType(ChannelType.RSS).stream()
				.map(ChannelEntity::getChannelId)
				.map(jda::getTextChannelById)
				.filter(Objects::nonNull)
				.toList();
		if(channels.isEmpty()){
			log.info("No channels configured, skipping");
			return;
		}
		
		var feeds = rssRepository.findAll().stream().collect(Collectors.groupingBy(RssEntity::getUrl));
		if(feeds.isEmpty()){
			log.info("No feeds configured, skipping");
			return;
		}
		
		var processedEntities = feeds.entrySet().stream()
				.flatMap(entry -> processFeed(entry.getKey(), entry.getValue(), filterChannels(channels, entry.getValue())))
				.toList();
		
		rssRepository.saveAll(processedEntities);
	}
	
	@NotNull
	private Collection<TextChannel> filterChannels(@NotNull List<TextChannel> channels, @NotNull List<RssEntity> entities){
		var guildIds = entities.stream().map(RssEntity::getGuild).map(GuildEntity::getId).collect(Collectors.toSet());
		return channels.stream()
				.filter(c -> guildIds.contains(c.getGuild().getIdLong()))
				.toList();
	}
	
	@NotNull
	private Stream<RssEntity> processFeed(@NotNull String feedUrl, @NotNull Collection<RssEntity> entities, @NotNull Collection<TextChannel> channels){
		try{
			log.info("Processing feed {}", feedUrl);
			
			if(channels.isEmpty()){
				log.warn("No channels defined, skipping");
				return entities.stream();
			}
			
			var lastFeedDate = entities.stream()
					.map(RssEntity::getLastPublicationDate)
					.filter(Objects::nonNull)
					.findFirst()
					.map(Instant::toEpochMilli)
					.orElse(-1L);
			
			var feedOptional = getFeed(feedUrl);
			if(feedOptional.isEmpty()){
				log.warn("Failed to get feed");
				return Stream.of();
			}
			var feed = feedOptional.get();
			var entries = feed.getEntries();
			var newEntries = entries.stream()
					.sorted(this::sortByDate)
					.filter(entry -> lastFeedDate < extractEntryTimestamp(entry))
					.toList();
			
			log.info("Found {} new entries in feed {}", newEntries.size(), feedUrl);
			newEntries.stream()
					.map(e -> createEmbed(feed, e))
					.forEach(embed -> sendEntry(embed, channels));
			
			newEntries.stream()
					.flatMap(entry -> extractDate(entry).stream())
					.max(Comparator.comparingLong(Instant::toEpochMilli))
					.ifPresent(instant -> entities.forEach(e -> e.setLastPublicationDate(instant)));
			
			Optional.ofNullable(feed.getTitle()).ifPresent(title -> entities.forEach(e -> e.setTitle(title)));
			
			return entities.stream();
		}
		catch(Exception e){
			log.error("Failed to process feed {}", feedUrl, e);
			return Stream.empty();
		}
	}
	
	@NotNull
	private Optional<Instant> extractDate(@NotNull SyndEntry entry){
		return Optional.ofNullable(entry.getPublishedDate())
				.or(() -> Optional.ofNullable(entry.getUpdatedDate()))
				.map(Date::toInstant);
	}
	
	private long extractEntryTimestamp(@NotNull SyndEntry entry){
		return extractDate(entry)
				.map(Instant::toEpochMilli)
				.orElse(0L);
	}
	
	@NotNull
	private Optional<SyndFeed> getFeed(@NotNull String url) throws RequestFailedException{
		var response = HttpUtils.withStatusOkAndBody(client.get()
				.uri(url)
				.retrieve()
				.toEntity(ByteArrayResource.class)
				.blockOptional()
				.orElseThrow(() -> new RequestFailedException("Failed to get RSS from " + url)));
		
		try{
			var reader = new XmlReader(new ByteArrayInputStream(response.getByteArray()));
			var feed = new SyndFeedInput().build(reader);
			return Optional.ofNullable(feed);
		}
		catch(IOException | FeedException e){
			log.error("Failed to parse RSS feed", e);
			return Optional.empty();
		}
	}
	
	private void sendEntry(@NotNull MessageEmbed embed, @NotNull Collection<TextChannel> channels){
		channels.forEach(c -> JDAWrappers.message(c, embed).submit());
	}
	
	@NotNull
	private MessageEmbed createEmbed(@NotNull SyndFeed feed, @NotNull SyndEntry entry){
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
		
		return builder.build();
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
	public void executeGuild(@NotNull Guild guild){
	}
	
	@Override
	public long getPeriod(){
		return 15;
	}
	
	@Override
	@NotNull
	public String getName(){
		return "RSS";
	}
	
	@Override
	@NotNull
	public ChronoUnit getPeriodUnit(){
		return ChronoUnit.MINUTES;
	}
}
