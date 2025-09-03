package fr.rakambda.rsndiscord.spring.schedule.impl.anilist;

import fr.rakambda.rsndiscord.spring.api.anilist.AnilistService;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.notification.AiringNotification;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.notification.MediaDataChangeNotification;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.notification.MediaDeletionNotification;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.notification.MediaMergeNotification;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.notification.Notification;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.notification.NotificationType;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.notification.RelatedMediaAdditionNotification;
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
import java.awt.*;
import java.time.Instant;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class AnilistNotificationRunner extends AnilistWrappedTriggerTask{
	private static final Set<NotificationType> NOTIFICATION_TYPES = Set.of(NotificationType.AIRING, NotificationType.RELATED_MEDIA_ADDITION);
	
	private final ChannelRepository channelRepository;
	private final AnilistRepository anilistRepository;
	private final LocalizationService localizationService;
	private final AnilistService anilistService;
	
	@Autowired
	public AnilistNotificationRunner(JDA jda, ChannelRepository channelRepository, AnilistRepository anilistRepository, LocalizationService localizationService, AnilistService anilistService){
		super(jda, localizationService);
		this.channelRepository = channelRepository;
		this.anilistRepository = anilistRepository;
		this.localizationService = localizationService;
		this.anilistService = anilistService;
	}
	
	@Override
	@NonNull
	public String getId(){
		return "anilist.notification";
	}
	
	@Override
	@NonNull
	protected String getName(){
		return "Anilist notification";
	}
	
	@Override
	protected long getPeriod(){
		return 4;
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
			
			var lastNotificationDate = Optional.ofNullable(entity.getLastNotificationDate())
					.filter(i -> i.isBefore(Instant.now()))
					.orElse(null);
			var notifications = anilistService.getAllNotifications(user.getIdLong(), NOTIFICATION_TYPES, true).stream()
					.filter(e -> isNewer(e, lastNotificationDate))
					.sorted(Comparator.comparing(e -> extractDate(e).orElse(Instant.EPOCH)))
					.toList();
			
			if(!notifications.isEmpty()){
				sendUserElements(user, notifications, channels);
			}
			
			notifications.stream()
					.map(this::extractDate)
					.flatMap(Optional::stream)
					.filter(i -> i.isBefore(Instant.now()))
					.max(Comparator.naturalOrder())
					.ifPresent(entity::setLastNotificationDate);
			
			return Stream.of(entity);
		}
		catch(Throwable e){
			log.error("Failed to process user {}", entity, e);
			return Stream.empty();
		}
	}
	
	private boolean isNewer(@NonNull Notification notification, @Nullable Instant reference){
		if(Objects.isNull(reference)){
			return true;
		}
		return extractDate(notification)
				.map(date -> date.isAfter(reference))
				.orElse(false);
	}
	
	@NonNull
	private Optional<Instant> extractDate(@NonNull Notification notification){
		return Optional.ofNullable(notification.getCreatedAt()).map(ChronoZonedDateTime::toInstant);
	}
	
	private void sendUserElements(@NonNull User user, @NonNull Collection<Notification> notifications, @NonNull Collection<GuildMessageChannel> channels){
		channels.stream()
				.filter(c -> JDAWrappers.isMember(c.getGuild(), user.getIdLong()))
				.forEach(c -> sendUserElements(c, user, notifications));
	}
	
	private void sendUserElements(@NonNull GuildMessageChannel channel, @NonNull User user, @NonNull Collection<Notification> notifications){
		notifications.stream()
				.map(n -> buildEmbed(n, user, channel.getGuild().getLocale()))
				.forEach(e -> JDAWrappers.message(channel, user.getAsMention()).embed(e).submit());
	}
	
	@NonNull
	private MessageEmbed buildEmbed(@NonNull Notification notification, @NonNull User user, @NonNull DiscordLocale locale){
		if(notification instanceof AiringNotification airingNotification){
			return buildTypeEmbed(airingNotification, user, locale);
		}
		if(notification instanceof RelatedMediaAdditionNotification relatedMediaAdditionNotification){
			return buildTypeEmbed(relatedMediaAdditionNotification, user, locale);
		}
		if(notification instanceof MediaDataChangeNotification mediaDataChangeNotification){
			return buildTypeEmbed(mediaDataChangeNotification, user, locale);
		}
		if(notification instanceof MediaMergeNotification mediaMergeNotification){
			return buildTypeEmbed(mediaMergeNotification, user, locale);
		}
		if(notification instanceof MediaDeletionNotification mediaDeletionNotification){
			return buildTypeEmbed(mediaDeletionNotification, user);
		}
		throw new IllegalStateException("Unknown type " + notification.getClass().getSimpleName());
	}
	
	@NonNull
	private MessageEmbed buildTypeEmbed(@NonNull MediaDeletionNotification notification, @NonNull User user){
		return new EmbedBuilder()
				.setAuthor(user.getName(), null, user.getAvatarUrl())
				.setTitle("Media entry deleted")
				.setColor(Color.RED)
				.addField("Deleted titles", notification.getDeletedMediaTitle(), true)
				.addField("Context", notification.getContext(), true)
				.addField("Reason", notification.getReason(), true)
				.setTimestamp(notification.getCreatedAt())
				.build();
	}
	
	@NonNull
	private MessageEmbed buildTypeEmbed(@NonNull MediaMergeNotification notification, @NonNull User user, @NonNull DiscordLocale locale){
		var deletedTitles = notification.getDeletedMediaTitles().stream()
				.map("`%s`"::formatted)
				.collect(Collectors.joining(", "));
		
		var builder = new EmbedBuilder()
				.setAuthor(user.getName(), null, user.getAvatarUrl())
				.setTitle("Media entries merged", notification.getMedia().getUrl())
				.setColor(Color.ORANGE)
				.addField("Deleted titles", deletedTitles, true)
				.addField("Context", notification.getContext(), true)
				.addField("Reason", notification.getReason(), true)
				.addBlankField(false)
				.addField(localizationService.translate(locale, "anilist.media"), "", false)
				.setTimestamp(notification.getCreatedAt());
		
		fillEmbed(builder, notification.getMedia(), locale);
		
		return builder.build();
	}
	
	@NonNull
	private MessageEmbed buildTypeEmbed(@NonNull MediaDataChangeNotification notification, @NonNull User user, @NonNull DiscordLocale locale){
		var builder = new EmbedBuilder()
				.setAuthor(user.getName(), null, user.getAvatarUrl())
				.setTitle("Media data changed", notification.getMedia().getUrl())
				.setColor(Color.ORANGE)
				.addField("Context", notification.getContext(), true)
				.addField("Reason", notification.getReason(), true)
				.addBlankField(false)
				.addField(localizationService.translate(locale, "anilist.media"), "", false)
				.setTimestamp(notification.getCreatedAt());
		
		fillEmbed(builder, notification.getMedia(), locale);
		
		return builder.build();
	}
	
	@NonNull
	private MessageEmbed buildTypeEmbed(@NonNull RelatedMediaAdditionNotification notification, @NonNull User user, @NonNull DiscordLocale locale){
		var builder = new EmbedBuilder()
				.setAuthor(user.getName(), null, user.getAvatarUrl())
				.setTitle(localizationService.translate(locale, "anilist.related"), notification.getMedia().getUrl())
				.setColor(Color.PINK)
				.addField(localizationService.translate(locale, "anilist.media"), "", false)
				.setTimestamp(notification.getCreatedAt());
		
		fillEmbed(builder, notification.getMedia(), locale);
		
		return builder.build();
	}
	
	@NonNull
	private MessageEmbed buildTypeEmbed(@NonNull AiringNotification notification, @NonNull User user, @NonNull DiscordLocale locale){
		var builder = new EmbedBuilder()
				.setAuthor(user.getName(), null, user.getAvatarUrl())
				.setTitle(localizationService.translate(locale, "anilist.release"), notification.getMedia().getUrl())
				.setColor(Color.GREEN)
				.addField(localizationService.translate(locale, "anilist.episode"), String.valueOf(notification.getEpisode()), true)
				.addBlankField(false)
				.addField(localizationService.translate(locale, "anilist.media"), "", false)
				.setTimestamp(notification.getCreatedAt());
		
		fillEmbed(builder, notification.getMedia(), locale);
		
		return builder.build();
	}
	
	@NonNull
	private Collection<GuildMessageChannel> getChannels(@NonNull JDA jda){
		return channelRepository.findAllByType(ChannelType.ANILIST_NOTIFICATION).stream()
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
