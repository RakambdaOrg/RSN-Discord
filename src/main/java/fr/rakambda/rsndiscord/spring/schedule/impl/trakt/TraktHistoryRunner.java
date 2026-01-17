package fr.rakambda.rsndiscord.spring.schedule.impl.trakt;

import fr.rakambda.rsndiscord.spring.api.exceptions.RequestFailedException;
import fr.rakambda.rsndiscord.spring.api.themoviedb.TheMovieDbService;
import fr.rakambda.rsndiscord.spring.api.themoviedb.TmdbImageSize;
import fr.rakambda.rsndiscord.spring.api.themoviedb.model.MovieDetails;
import fr.rakambda.rsndiscord.spring.api.themoviedb.model.Season;
import fr.rakambda.rsndiscord.spring.api.themoviedb.model.TvDetails;
import fr.rakambda.rsndiscord.spring.api.trakt.TraktService;
import fr.rakambda.rsndiscord.spring.api.trakt.response.data.history.MediaIds;
import fr.rakambda.rsndiscord.spring.api.trakt.response.data.history.UserHistory;
import fr.rakambda.rsndiscord.spring.api.trakt.response.data.history.UserMovieHistory;
import fr.rakambda.rsndiscord.spring.api.trakt.response.data.history.UserSeriesHistory;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.schedule.WrappedTriggerTask;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.entity.TraktEntity;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import fr.rakambda.rsndiscord.spring.storage.repository.TraktRepository;
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
import org.springframework.stereotype.Component;
import java.awt.*;
import java.time.Instant;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@Slf4j
public class TraktHistoryRunner extends WrappedTriggerTask{
	private static final Comparator<UserHistory> USER_HISTORY_COMPARATOR = Comparator
			.<UserHistory, Instant> comparing(h -> extractDate(h).orElse(Instant.EPOCH))
			.thenComparing(h -> extractSeason(h).orElse(0))
			.thenComparing(h -> extractEpisode(h).orElse(0));
	private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	private final ChannelRepository channelRepository;
	private final TraktRepository traktRepository;
	private final TraktService traktService;
	private final LocalizationService localizationService;
	private final TheMovieDbService theMovieDbService;
	
	public TraktHistoryRunner(@NonNull JDA jda, ChannelRepository channelRepository, TraktRepository traktRepository, TraktService traktService, LocalizationService localizationService, TheMovieDbService theMovieDbService){
		super(jda);
		this.channelRepository = channelRepository;
		this.traktRepository = traktRepository;
		this.traktService = traktService;
		this.localizationService = localizationService;
		this.theMovieDbService = theMovieDbService;
	}
	
	@Override
	@NonNull
	public String getId(){
		return "trakt.history";
	}
	
	@Override
	@NonNull
	protected String getName(){
		return "Trakt user history";
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
		
		traktRepository.saveAll(processedEntities);
	}
	
	@NonNull
	private Stream<TraktEntity> processUser(@NonNull JDA jda, @NonNull TraktEntity entity, @NonNull Collection<GuildMessageChannel> channels){
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
			
			var lastActivityDate = Optional.ofNullable(entity.getLastActivityDate())
					.filter(i -> i.isBefore(Instant.now()))
					.orElse(null);
			var histories = traktService.getAllUserHistory(user.getIdLong(), lastActivityDate, null).stream()
					.filter(h -> isNewer(h, lastActivityDate))
					.sorted(USER_HISTORY_COMPARATOR)
					.toList();
			
			sendUserElements(user, histories, channels);
			
			histories.stream()
					.map(TraktHistoryRunner::extractDate)
					.flatMap(Optional::stream)
					.filter(i -> i.isBefore(Instant.now()))
					.max(Comparator.naturalOrder())
					.ifPresent(entity::setLastActivityDate);
			
			return Stream.of(entity);
		}
		catch(Exception e){
			log.error("Failed to process user {}", entity, e);
			return Stream.empty();
		}
	}
	
	private void sendUserElements(@NonNull User user, @NonNull Collection<UserHistory> histories, @NonNull Collection<GuildMessageChannel> channels){
		channels.stream()
				.filter(c -> JDAWrappers.isMember(c.getGuild(), user.getIdLong()))
				.forEach(c -> sendUserElements(c, user, histories));
	}
	
	private void sendUserElements(@NonNull GuildMessageChannel channel, @NonNull User user, @NonNull Collection<UserHistory> histories){
		histories.stream()
				.map(h -> buildEmbed(h, user, channel.getGuild().getLocale()))
				.forEach(e -> JDAWrappers.message(channel, e).submit());
	}
	
	@NonNull
	private Optional<MovieDetails> getMovieDetails(long id){
		try{
			return Optional.of(theMovieDbService.getMovieDetails(id));
		}
		catch(RequestFailedException e){
			log.warn("Failed to get movie details", e);
			return Optional.empty();
		}
	}
	
	@NonNull
	private Optional<TvDetails> getTvDetails(long id){
		try{
			return Optional.of(theMovieDbService.getTvDetails(id));
		}
		catch(RequestFailedException e){
			log.warn("Failed to get tv details", e);
			return Optional.empty();
		}
	}
	
	@NonNull
	private MessageEmbed buildEmbed(@NonNull UserHistory history, @NonNull User user, @NonNull DiscordLocale locale){
		return switch(history){
			case UserMovieHistory userMovieHistory -> buildTypeEmbed(userMovieHistory, user, locale);
			case UserSeriesHistory userSeriesHistory -> buildTypeEmbed(userSeriesHistory, user, locale);
		};
	}
	
	@NonNull
	private MessageEmbed buildTypeEmbed(@NonNull UserMovieHistory history, @NonNull User user, @NonNull DiscordLocale locale){
		var movie = history.getMovie();
		
		var movieDetails = Optional.ofNullable(history.getMovie().getIds())
				.map(MediaIds::getTmdb)
				.flatMap(this::getMovieDetails);
		
		var builder = new EmbedBuilder()
				.setAuthor(user.getName(), null, user.getAvatarUrl())
				.setTitle(localizationService.translate(locale, "trakt.watched.movie"), movie.getTrailer())
				.setColor(Color.GREEN)
				.addField(localizationService.translate(locale, "trakt.title"), movie.getTitle(), true)
				.addField(localizationService.translate(locale, "trakt.year"), Integer.toString(movie.getYear()), true)
				.addField(localizationService.translate(locale, "trakt.status"), movie.getStatus(), true)
				.addField(localizationService.translate(locale, "trakt.aired"), movie.getReleased().format(DATE_FORMAT), true)
				.addField(localizationService.translate(locale, "trakt.genres"), String.join(", ", movie.getGenres()), true)
				.addField(localizationService.translate(locale, "trakt.overview"), movie.getOverview(), false)
				.addBlankField(false)
				.addField(localizationService.translate(locale, "trakt.watched"), history.getWatchedAt().format(DATETIME_FORMAT), true)
				.setFooter(Long.toString(history.getId()))
				.setTimestamp(history.getWatchedAt());
		
		movieDetails.map(MovieDetails::getPosterPath)
				.map(p -> theMovieDbService.getImageURL(p, TmdbImageSize.ORIGINAL))
				.ifPresent(builder::setImage);
		
		return builder.build();
	}
	
	@NonNull
	private MessageEmbed buildTypeEmbed(@NonNull UserSeriesHistory history, @NonNull User user, @NonNull DiscordLocale locale){
		var show = history.getShow();
		var episode = history.getEpisode();
		
		var tvDetails = Optional.ofNullable(show.getIds())
				.map(MediaIds::getTmdb)
				.flatMap(this::getTvDetails);
		var seasonDetails = tvDetails.stream().map(TvDetails::getSeasons)
				.flatMap(Collection::stream)
				.filter(s -> Objects.equals(s.getSeasonNumber(), episode.getSeason()))
				.findFirst();
		
		var totalSeason = tvDetails.map(TvDetails::getNumberOfSeasons);
		var seasonProgress = totalSeason.map(t -> "%d/%d".formatted(episode.getSeason(), t)).orElseGet(() -> Integer.toString(episode.getSeason()));
		
		var totalEpisodes = seasonDetails.map(Season::getEpisodeCount);
		var episodeProgress = totalEpisodes.map(t -> "%d/%d".formatted(episode.getNumber(), t)).orElseGet(() -> Integer.toString(episode.getNumber()));
		
		var builder = new EmbedBuilder()
				.setAuthor(user.getName(), null, user.getAvatarUrl())
				.setTitle(localizationService.translate(locale, "trakt.watched.episode"), show.getTrailer())
				.addField(localizationService.translate(locale, "trakt.season"), seasonProgress, true)
				.addField(localizationService.translate(locale, "trakt.episode"), episodeProgress, true)
				.addField(localizationService.translate(locale, "trakt.aired"), episode.getFirstAired().format(DATETIME_FORMAT), true);
		
		Optional.ofNullable(episode.getOverview())
				.ifPresent(o -> builder.addField(localizationService.translate(locale, "trakt.overview"), o, false));
		
		builder.addBlankField(false)
				.addField(localizationService.translate(locale, "trakt.title"), show.getTitle(), true)
				.addField(localizationService.translate(locale, "trakt.year"), Integer.toString(show.getYear()), true);
		
		totalSeason.ifPresent(s -> builder.addField(localizationService.translate(locale, "trakt.seasons"), Integer.toString(s), true));
		
		builder.addField(localizationService.translate(locale, "trakt.episodes"), Integer.toString(show.getAiredEpisodes()), true)
				.addField(localizationService.translate(locale, "trakt.status"), show.getStatus(), true)
				.addField(localizationService.translate(locale, "trakt.genres"), String.join(", ", show.getGenres()), true);
		
		Optional.ofNullable(show.getOverview()).ifPresent(o -> builder.addField(localizationService.translate(locale, "trakt.overview"), o, false));
		
		builder
				.addBlankField(false)
				.addField(localizationService.translate(locale, "trakt.watched"), history.getWatchedAt().format(DATETIME_FORMAT), true)
				.setFooter(Long.toString(history.getId()))
				.setTimestamp(history.getWatchedAt());
		
		seasonDetails.map(Season::getPosterPath)
				.or(() -> tvDetails.map(TvDetails::getPosterPath))
				.map(p -> theMovieDbService.getImageURL(p, TmdbImageSize.ORIGINAL))
				.ifPresent(builder::setImage);
		
		totalEpisodes
				.filter(total -> episode.getNumber() >= total)
				.ifPresent(_ -> builder.setColor(Color.GREEN));
		
		return builder.build();
	}
	
	private boolean isNewer(@NonNull UserHistory history, @Nullable Instant reference){
		if(Objects.isNull(reference)){
			return true;
		}
		return extractDate(history)
				.map(date -> date.isAfter(reference))
				.orElse(false);
	}
	
	@NonNull
	private static Optional<Instant> extractDate(@NonNull UserHistory history){
		return Optional.ofNullable(history.getWatchedAt()).map(ChronoZonedDateTime::toInstant);
	}
	
	@NonNull
	private static Optional<Integer> extractSeason(@NonNull UserHistory history){
		if(history instanceof UserSeriesHistory serieHistory){
			return Optional.of(serieHistory.getEpisode().getSeason());
		}
		return Optional.empty();
	}
	
	@NonNull
	private static Optional<Integer> extractEpisode(@NonNull UserHistory history){
		if(history instanceof UserSeriesHistory serieHistory){
			return Optional.of(serieHistory.getEpisode().getNumber());
		}
		return Optional.empty();
	}
	
	@NonNull
	private Collection<GuildMessageChannel> getChannels(@NonNull JDA jda){
		return channelRepository.findAllByType(ChannelType.TRAKT_MEDIA_CHANGE).stream()
				.map(entity -> JDAWrappers.findChannel(jda, entity.getChannelId()))
				.flatMap(Optional::stream)
				.toList();
	}
	
	@NonNull
	private Collection<TraktEntity> getUsers(){
		return traktRepository.findAllByEnabledIsTrue();
	}
	
	@Override
	protected void executeGuild(@NonNull Guild guild){
	}
}
