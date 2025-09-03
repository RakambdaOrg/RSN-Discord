package fr.rakambda.rsndiscord.spring.schedule.impl.anilist;

import fr.rakambda.rsndiscord.spring.api.anilist.AnilistService;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.MediaList;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media.AnimeMedia;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media.MangaMedia;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media.Media;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.storage.entity.AnilistEntity;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.repository.AnilistRepository;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.time.Instant;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class AnilistMediaListRunner extends AnilistWrappedTriggerTask{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	private final ChannelRepository channelRepository;
	private final AnilistRepository anilistRepository;
	private final LocalizationService localizationService;
	private final AnilistService anilistService;
	
	@Autowired
	public AnilistMediaListRunner(JDA jda, ChannelRepository channelRepository, AnilistRepository anilistRepository, LocalizationService localizationService, AnilistService anilistService){
		super(jda, localizationService);
		this.channelRepository = channelRepository;
		this.anilistRepository = anilistRepository;
		this.localizationService = localizationService;
		this.anilistService = anilistService;
	}
	
	@Override
	@NonNull
	public String getId(){
		return "anilist.media";
	}
	
	@Override
	@NonNull
	protected String getName(){
		return "Anilist media list";
	}
	
	@Override
	protected long getPeriod(){
		return 12;
	}
	
	@Override
	@NonNull
	protected TemporalUnit getPeriodUnit(){
		return ChronoUnit.HOURS;
	}
	
	@Override
	protected void executeGlobal(@NonNull JDA jda){
		var channels = getChannels(jda);
		if(channels.isEmpty()){
			log.info("No channels configured, skipping");
			return;
		}
		
		var entities = getUsers();
		if(entities.isEmpty()){
			log.info("No entities configured, skipping");
			return;
		}
		
		var processedEntities = entities.stream()
				.flatMap(e -> processUser(jda, e, channels))
				.toList();
		
		anilistRepository.saveAll(processedEntities);
	}
	
	@NonNull
	private Stream<AnilistEntity> processUser(@NonNull JDA jda, @NonNull AnilistEntity entity, @NonNull Collection<GuildMessageChannel> channels){
		try{
			var userOptional = JDAWrappers.findUser(jda, entity.getId());
			if(userOptional.isEmpty()){
				log.warn("Failed to get user {}, skipping", entity.getId());
				return Stream.empty();
			}
			var user = userOptional.get();
			log.info("Processing user {}", user);
			
			if(channels.isEmpty()){
				log.warn("No channels defined, skipping");
				return Stream.empty();
			}
			
			var lastMediaListDate = Optional.ofNullable(entity.getLastMediaListDate())
					.filter(i -> i.isBefore(Instant.now()))
					.orElse(null);
			var mediaLists = anilistService.getAllMediaList(user.getIdLong(), entity.getUserId()).stream()
					.filter(e -> isNewer(e, lastMediaListDate))
					.sorted(Comparator.comparing(e -> extractDate(e).orElse(Instant.EPOCH)))
					.toList();
			
			if(!mediaLists.isEmpty()){
				sendUserElements(user, mediaLists, channels);
			}
			
			mediaLists.stream()
					.map(this::extractDate)
					.flatMap(Optional::stream)
					.filter(i -> i.isBefore(Instant.now()))
					.max(Comparator.naturalOrder())
					.ifPresent(entity::setLastMediaListDate);
			
			return Stream.of(entity);
		}
		catch(Throwable e){
			log.error("Failed to process user {}", entity, e);
			return Stream.empty();
		}
	}
	
	private boolean isNewer(@NonNull MediaList mediaList, @Nullable Instant reference){
		if(Objects.isNull(reference)){
			return true;
		}
		return extractDate(mediaList)
				.map(date -> date.isAfter(reference))
				.orElse(false);
	}
	
	@NonNull
	private Optional<Instant> extractDate(@NonNull MediaList mediaList){
		return Optional.ofNullable(mediaList.getUpdatedAt()).map(ChronoZonedDateTime::toInstant);
	}
	
	private void sendUserElements(@NonNull User user, @NonNull Collection<MediaList> mediaLists, @NonNull Collection<GuildMessageChannel> channels){
		channels.stream()
				.filter(c -> JDAWrappers.isMember(c.getGuild(), user.getIdLong()))
				.forEach(c -> sendUserElements(c, user, mediaLists));
	}
	
	private void sendUserElements(@NonNull GuildMessageChannel channel, @NonNull User user, @NonNull Collection<MediaList> mediaLists){
		mediaLists.stream()
				.map(ml -> buildEmbed(ml, user, channel.getGuild().getLocale()))
				.forEach(e -> JDAWrappers.message(channel, e).submit());
	}
	
	@NonNull
	private MessageEmbed buildEmbed(@NonNull MediaList mediaList, @NonNull User user, @NonNull DiscordLocale locale){
		var media = mediaList.getMedia();
		var totalElements = getTotalElements(media).map(Objects::toString).orElse("?");
		
		var builder = new EmbedBuilder()
				.setAuthor(user.getName(), null, user.getAvatarUrl())
				.setTitle(localizationService.translate(locale, "anilist.list-info"), media.getUrl())
				.setColor(mediaList.getStatus().getColor())
				.addField(localizationService.translate(locale, "anilist.list-status"), mediaList.getStatus().getValue(), true)
				.setTimestamp(mediaList.getUpdatedAt());
		
		Optional.ofNullable(mediaList.getScore())
				.map("%d/100"::formatted)
				.ifPresent(s -> builder.addField(localizationService.translate(locale, "anilist.list-score"), s, true));
		
		Optional.of(mediaList.isPrivateItem())
				.filter(p -> p)
				.ifPresent(p -> builder.addField(localizationService.translate(locale, "anilist.list-private"), "\uD83D\uDD12 Yes", true));
		
		mediaList.getStartedAt().asDate()
				.map(DF::format)
				.ifPresent(d -> builder.addField(localizationService.translate(locale, "anilist.list-started"), d, true));
		
		mediaList.getCompletedAt().asDate()
				.ifPresent(date -> {
					var days = mediaList.getStartedAt().durationTo(date)
							.map(d -> d.get(ChronoUnit.DAYS));
					
					if(days.isPresent()){
						builder.addField(localizationService.translate(locale, "anilist.list-complete"), "%s (%d days)".formatted(date.format(DF), days.get()), true);
					}
					else{
						builder.addField(localizationService.translate(locale, "anilist.list-complete"), date.format(DF), true);
					}
				});
		
		builder.addField(localizationService.translate(locale, "anilist.list-progress"), mediaList.getProgress() + "/" + totalElements, true);
		
		if(media instanceof MangaMedia mangaMedia){
			if(Objects.nonNull(mediaList.getProgressVolumes())){
				var totalVolumes = Optional.ofNullable(mangaMedia.getVolumes())
						.map(Object::toString)
						.orElse("?");
				builder.addField(localizationService.translate(locale, "anilist.list-volumes"), mediaList.getProgressVolumes() + "/" + totalVolumes, true);
			}
		}
		
		Optional.ofNullable(mediaList.getRepeat())
				.filter(r -> r > 0)
				.map(Object::toString)
				.ifPresent(r -> builder.addField(localizationService.translate(locale, "anilist.list-repeat"), r, true));
		
		Optional.ofNullable(mediaList.getCustomLists())
				.filter(l -> !l.isEmpty())
				.map(l -> l.entrySet().stream()
						.filter(k -> Objects.nonNull(k.getValue()) && k.getValue())
						.map(Map.Entry::getKey)
						.collect(Collectors.joining(", ")))
				.ifPresent(l -> builder.addField(localizationService.translate(locale, "anilist.list-custom"), l, true));
		
		Optional.ofNullable(mediaList.getNotes())
				.filter(StringUtils::hasText)
				.ifPresent(n -> builder.addField(localizationService.translate(locale, "anilist.list-notes"), n, false));
		
		builder.addBlankField(false);
		
		fillEmbed(builder, media, locale);
		
		return builder.build();
	}
	
	@NonNull
	private Optional<Integer> getTotalElements(@NonNull Media media){
		if(media instanceof AnimeMedia animeMedia){
			return Optional.ofNullable(animeMedia.getEpisodes());
		}
		if(media instanceof MangaMedia mangaMedia){
			return Optional.ofNullable(mangaMedia.getChapters());
		}
		throw new IllegalStateException("Unknown type " + media.getClass().getSimpleName());
	}
	
	@NonNull
	private Collection<GuildMessageChannel> getChannels(@NonNull JDA jda){
		return channelRepository.findAllByType(ChannelType.ANILIST_MEDIA_CHANGE).stream()
				.map(entity -> JDAWrappers.findChannel(jda, entity.getChannelId()))
				.flatMap(Optional::stream)
				.toList();
	}
	
	@NonNull
	private Collection<AnilistEntity> getUsers(){
		return anilistRepository.findAllByEnabledIsTrue();
	}
	
	@Override
	protected void executeGuild(@NonNull Guild guild){
	}
}
