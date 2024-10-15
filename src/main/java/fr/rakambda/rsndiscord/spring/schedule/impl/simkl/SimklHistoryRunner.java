package fr.rakambda.rsndiscord.spring.schedule.impl.simkl;

import fr.rakambda.rsndiscord.spring.api.exceptions.RequestFailedException;
import fr.rakambda.rsndiscord.spring.api.simkl.SimklService;
import fr.rakambda.rsndiscord.spring.api.simkl.response.history.MediaIds;
import fr.rakambda.rsndiscord.spring.api.simkl.response.history.UserAnimeHistory;
import fr.rakambda.rsndiscord.spring.api.simkl.response.history.UserHistory;
import fr.rakambda.rsndiscord.spring.api.simkl.response.history.UserMovieHistory;
import fr.rakambda.rsndiscord.spring.api.simkl.response.history.UserSeriesHistory;
import fr.rakambda.rsndiscord.spring.api.themoviedb.TheMovieDbService;
import fr.rakambda.rsndiscord.spring.api.themoviedb.TmdbImageSize;
import fr.rakambda.rsndiscord.spring.api.themoviedb.model.Genre;
import fr.rakambda.rsndiscord.spring.api.themoviedb.model.MovieDetails;
import fr.rakambda.rsndiscord.spring.api.themoviedb.model.TvDetails;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.schedule.WrappedTriggerTask;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.entity.SimklEntity;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import fr.rakambda.rsndiscord.spring.storage.repository.SimklRepository;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
public class SimklHistoryRunner extends WrappedTriggerTask{
	private static final Comparator<UserHistory> USER_HISTORY_COMPARATOR = Comparator.comparing(h -> extractDate(h).orElse(Instant.EPOCH));
	private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	private final ChannelRepository channelRepository;
	private final SimklRepository simklRepository;
	private final SimklService simklService;
	private final LocalizationService localizationService;
	private final TheMovieDbService theMovieDbService;
	
	public SimklHistoryRunner(@NotNull JDA jda, ChannelRepository channelRepository, SimklRepository simklRepository, SimklService simklService, LocalizationService localizationService, TheMovieDbService theMovieDbService){
		super(jda);
		this.channelRepository = channelRepository;
		this.simklRepository = simklRepository;
		this.simklService = simklService;
		this.localizationService = localizationService;
		this.theMovieDbService = theMovieDbService;
	}
	
	@Override
	@NotNull
	public String getId(){
		return "simkl.history";
	}
	
	@Override
	@NotNull
	protected String getName(){
		return "Simkl user history";
	}
	
	@Override
	protected long getPeriod(){
		return 12;
	}
	
	@Override
	@NotNull
	protected TemporalUnit getPeriodUnit(){
		return ChronoUnit.HOURS;
	}
	
	@Override
	protected void executeGlobal(@NotNull JDA jda){
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
		
		simklRepository.saveAll(processedEntities);
	}
	
	@NotNull
	private Stream<SimklEntity> processUser(@NotNull JDA jda, @NotNull SimklEntity entity, @NotNull Collection<GuildMessageChannel> channels){
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
			var histories = simklService.getAllUserHistory(user.getIdLong(), lastActivityDate).stream()
					.filter(h -> isNewer(h, lastActivityDate))
					.sorted(USER_HISTORY_COMPARATOR)
					.toList();
			
			sendUserElements(user, histories, channels);
			
			histories.stream()
					.map(SimklHistoryRunner::extractDate)
					.flatMap(Optional::stream)
					.max(Comparator.naturalOrder())
					.ifPresent(entity::setLastActivityDate);
			
			return Stream.of(entity);
		}
		catch(Exception e){
			log.error("Failed to process user {}", entity, e);
			return Stream.empty();
		}
	}
	
	private void sendUserElements(@NotNull User user, @NotNull Collection<UserHistory> histories, @NotNull Collection<GuildMessageChannel> channels){
		channels.stream()
				.filter(c -> JDAWrappers.isMember(c.getGuild(), user.getIdLong()))
				.forEach(c -> sendUserElements(c, user, histories));
	}
	
	private void sendUserElements(@NotNull GuildMessageChannel channel, @NotNull User user, @NotNull Collection<UserHistory> histories){
		histories.stream()
				.map(h -> buildEmbed(h, user, channel.getGuild().getLocale()))
				.forEach(e -> JDAWrappers.message(channel, e).submit());
	}
	
	@NotNull
	private Optional<MovieDetails> getMovieDetails(long id){
		try{
			return Optional.of(theMovieDbService.getMovieDetails(id));
		}
		catch(RequestFailedException e){
			log.warn("Failed to get movie details", e);
			return Optional.empty();
		}
	}
	
	@NotNull
	private Optional<TvDetails> getTvDetails(long id){
		try{
			return Optional.of(theMovieDbService.getTvDetails(id));
		}
		catch(RequestFailedException e){
			log.warn("Failed to get tv details", e);
			return Optional.empty();
		}
	}
	
	@NotNull
	private MessageEmbed buildEmbed(@NotNull UserHistory history, @NotNull User user, @NotNull DiscordLocale locale){
		return switch(history){
			case UserMovieHistory userMovieHistory -> buildTypeEmbed(userMovieHistory, user, locale);
			case UserSeriesHistory userSeriesHistory -> buildTypeEmbed(userSeriesHistory, user, locale);
		};
	}
	
	@NotNull
	private MessageEmbed buildTypeEmbed(@NotNull UserMovieHistory history, @NotNull User user, @NotNull DiscordLocale locale){
		var movie = history.getMovie();
		
		var movieDetails = Optional.of(history.getMovie().getIds())
				.map(MediaIds::getTmdb)
				.flatMap(this::getMovieDetails);
		
		var builder = new EmbedBuilder()
				.setAuthor(user.getName(), null, user.getAvatarUrl())
				.setTitle(localizationService.translate(locale, "trakt.watched.movie"), getLink(history))
				.setColor(Color.GREEN)
				.addField(localizationService.translate(locale, "trakt.title"), movie.getTitle(), true)
				.addField(localizationService.translate(locale, "trakt.year"), Integer.toString(movie.getYear()), true);
		
		movieDetails.map(MovieDetails::getStatus)
				.ifPresent(o -> builder.addField(localizationService.translate(locale, "trakt.status"), o, true));
		movieDetails.map(MovieDetails::getReleaseDate)
				.ifPresent(o -> builder.addField(localizationService.translate(locale, "trakt.aired"), o.format(DATE_FORMAT), true));
		movieDetails.map(MovieDetails::getGenres)
				.ifPresent(o -> builder.addField(localizationService.translate(locale, "trakt.genres"), String.join(", ", o.stream().map(Genre::getName).toList()), true));
		movieDetails.map(MovieDetails::getOverview)
				.ifPresent(o -> builder.addField(localizationService.translate(locale, "trakt.overview"), o, false));
		
		builder.addBlankField(false)
				.addField(localizationService.translate(locale, "trakt.watched"), history.getLastWatchedAt().format(DATETIME_FORMAT), true)
				.setFooter(Long.toString(history.getId()))
				.setTimestamp(history.getLastWatchedAt());
		
		movieDetails.map(MovieDetails::getPosterPath)
				.map(p -> theMovieDbService.getImageURL(p, TmdbImageSize.ORIGINAL))
				.ifPresent(builder::setImage);
		
		return builder.build();
	}
	
	@NotNull
	private MessageEmbed buildTypeEmbed(@NotNull UserSeriesHistory history, @NotNull User user, @NotNull DiscordLocale locale){
		var show = history.getShow();
		
		var tvDetails = Optional.of(show.getIds())
				.map(MediaIds::getTmdb)
				.flatMap(this::getTvDetails);
		
		var totalSeason = tvDetails.map(TvDetails::getNumberOfSeasons);
		
		var totalEpisodes = Optional.ofNullable(history.getTotalEpisodesCount());
		var episodeProgress = totalEpisodes.map(t -> "%d/%d".formatted(history.getWatchedEpisodesCount(), t))
				.or(() -> Optional.ofNullable(history.getWatchedEpisodesCount()).map(i -> Integer.toString(i)))
				.orElse("Unknown");
		
		var builder = new EmbedBuilder()
				.setAuthor(user.getName(), null, user.getAvatarUrl())
				.setTitle(localizationService.translate(locale, "trakt.watched.episode"), getLink(history))
				.addField(localizationService.translate(locale, "trakt.episode"), episodeProgress, true);
		
		tvDetails.map(TvDetails::getFirstAirDate)
				.ifPresent(o -> builder.addField(localizationService.translate(locale, "trakt.aired"), o.format(DATETIME_FORMAT), true));
		
		builder.addBlankField(false)
				.addField(localizationService.translate(locale, "trakt.title"), show.getTitle(), true)
				.addField(localizationService.translate(locale, "trakt.year"), Integer.toString(show.getYear()), true);
		
		totalSeason.ifPresent(s -> builder.addField(localizationService.translate(locale, "trakt.seasons"), Integer.toString(s), true));
		
		Optional.ofNullable(history.getTotalEpisodesCount())
				.ifPresent(o -> builder.addField(localizationService.translate(locale, "trakt.episodes"), Integer.toString(o), true));
		tvDetails.map(TvDetails::getStatus)
				.ifPresent(o -> builder.addField(localizationService.translate(locale, "trakt.status"), o, true));
		tvDetails.map(TvDetails::getGenres)
				.ifPresent(o -> builder.addField(localizationService.translate(locale, "trakt.genres"), String.join(", ", o.stream().map(Genre::getName).toList()), true));
		tvDetails.map(TvDetails::getOverview)
				.ifPresent(o -> builder.addField(localizationService.translate(locale, "trakt.overview"), o, true));
		
		builder.addBlankField(false)
				.addField(localizationService.translate(locale, "trakt.watched"), history.getLastWatchedAt().format(DATETIME_FORMAT), true)
				.setFooter(Long.toString(history.getId()))
				.setTimestamp(history.getLastWatchedAt());
		
		tvDetails.map(TvDetails::getPosterPath)
				.map(p -> theMovieDbService.getImageURL(p, TmdbImageSize.ORIGINAL))
				.ifPresent(builder::setImage);
		
		return builder.build();
	}
	
	private String getLink(UserHistory history){
		var type = switch(history){
			case UserMovieHistory ignored -> "movies";
			case UserAnimeHistory ignored -> "anime";
			case UserSeriesHistory ignored -> "tv";
		};
		return "https://simkl.com/%s/%s".formatted(type, history.getId());
	}
	
	private boolean isNewer(@NotNull UserHistory history, @Nullable Instant reference){
		if(Objects.isNull(reference)){
			return true;
		}
		return extractDate(history)
				.map(date -> date.isAfter(reference))
				.orElse(false);
	}
	
	@NotNull
	private static Optional<Instant> extractDate(@NotNull UserHistory history){
		return Optional.ofNullable(history.getLastWatchedAt()).map(ChronoZonedDateTime::toInstant);
	}
	
	@NotNull
	private Collection<GuildMessageChannel> getChannels(@NotNull JDA jda){
		return channelRepository.findAllByType(ChannelType.SIMKL_MEDIA_CHANGE).stream()
				.map(entity -> JDAWrappers.findChannel(jda, entity.getChannelId()))
				.flatMap(Optional::stream)
				.toList();
	}
	
	@NotNull
	private Collection<SimklEntity> getUsers(){
		return simklRepository.findAllByEnabledIsTrue();
	}
	
	@Override
	protected void executeGuild(@NotNull Guild guild){
	}
}
