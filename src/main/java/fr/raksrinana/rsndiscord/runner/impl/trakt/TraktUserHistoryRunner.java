package fr.raksrinana.rsndiscord.runner.impl.trakt;

import fr.raksrinana.rsndiscord.api.themoviedb.TheMovieDBApi;
import fr.raksrinana.rsndiscord.api.themoviedb.model.MediaDetails;
import fr.raksrinana.rsndiscord.api.themoviedb.requests.ITMDBGetRequest;
import fr.raksrinana.rsndiscord.api.themoviedb.requests.MovieDetailsGetRequest;
import fr.raksrinana.rsndiscord.api.themoviedb.requests.TVDetailsGetRequest;
import fr.raksrinana.rsndiscord.api.trakt.TraktApi;
import fr.raksrinana.rsndiscord.api.trakt.model.users.history.MediaIds;
import fr.raksrinana.rsndiscord.api.trakt.model.users.history.UserHistory;
import fr.raksrinana.rsndiscord.api.trakt.model.users.history.UserMovieHistory;
import fr.raksrinana.rsndiscord.api.trakt.requests.users.UserHistoryPagedGetRequest;
import fr.raksrinana.rsndiscord.runner.api.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import fr.raksrinana.rsndiscord.utils.RequestException;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.stream.Collectors.toSet;

@ScheduledRunner
@Log4j2
public class TraktUserHistoryRunner extends ITraktPagedGetRunner<UserHistory, UserHistoryPagedGetRequest>{
	@NotNull
	@Override
	public String getName(){
		return "Trakt user history";
	}
	
	@Override
	public boolean isKeepOnlyNew(){
		return true;
	}
	
	@Override
	@NotNull
	public TimeUnit getPeriodUnit(){
		return HOURS;
	}
	
	@Override
	@NotNull
	public Set<TextChannel> getChannels(@NotNull JDA jda){
		return jda.getGuilds().stream()
				.flatMap(g -> Settings.get(g).getTraktConfiguration()
						.getMediaChangeChannel()
						.flatMap(ChannelConfiguration::getChannel)
						.stream())
				.collect(toSet());
	}
	
	@NotNull
	@Override
	public UserHistoryPagedGetRequest initQuery(@NotNull Member member){
		var username = TraktApi.getUsername(member)
				.orElseThrow(() -> new RuntimeException("Failed to get username for member " + member));
		var lastAccess = Settings.getGeneral().getTrakt()
				.getLastAccess(getFetcherID(), member.getIdLong())
				.map(UserDateConfiguration::getDate)
				.map(date -> date.plusNanos(1000L))
				.orElse(ZonedDateTime.now());
		return new UserHistoryPagedGetRequest(username, 1, lastAccess);
	}
	
	@Override
	public long getDelay(){
		return 7;
	}
	
	@Override
	@NotNull
	public String getFetcherID(){
		return "history";
	}
	
	@Override
	public long getPeriod(){
		return 1;
	}
	
	@Override
	public void buildMessage(@NotNull Guild guild, @NotNull EmbedBuilder builder, @NotNull User user, @NotNull UserHistory change){
		builder.setAuthor(user.getName(), null, user.getAvatarUrl());
		getTMDBInfos(change).ifPresentOrElse(mediaDetails -> change.fillEmbed(guild, builder, mediaDetails),
				() -> change.fillEmbed(guild, builder));
	}
	
	@NotNull
	private static Optional<MediaDetails> getTMDBInfos(@NotNull UserHistory change){
		Function<Long, ITMDBGetRequest<? extends MediaDetails>> requestBuilder = change instanceof UserMovieHistory
				? MovieDetailsGetRequest::new
				: TVDetailsGetRequest::new;
		return ofNullable(change.getIds())
				.map(MediaIds::getTmdb)
				.map(requestBuilder)
				.map(query -> {
					try{
						return TheMovieDBApi.getQuery(query);
					}
					catch(RequestException e){
						log.error("Failed to get extra movie infos for {}", change, e);
					}
					return null;
				});
	}
}
