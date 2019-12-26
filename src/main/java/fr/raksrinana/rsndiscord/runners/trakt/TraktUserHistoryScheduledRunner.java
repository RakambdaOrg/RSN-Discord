package fr.raksrinana.rsndiscord.runners.trakt;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import fr.raksrinana.rsndiscord.utils.RequestException;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.themoviedb.TMDBUtils;
import fr.raksrinana.rsndiscord.utils.themoviedb.model.MovieDetails;
import fr.raksrinana.rsndiscord.utils.themoviedb.model.TVDetails;
import fr.raksrinana.rsndiscord.utils.themoviedb.requests.MovieDetailsGetRequest;
import fr.raksrinana.rsndiscord.utils.themoviedb.requests.TVDetailsGetRequest;
import fr.raksrinana.rsndiscord.utils.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.utils.trakt.model.users.history.UserHistory;
import fr.raksrinana.rsndiscord.utils.trakt.model.users.history.UserMovieHistory;
import fr.raksrinana.rsndiscord.utils.trakt.model.users.history.UserSerieHistory;
import fr.raksrinana.rsndiscord.utils.trakt.requests.users.UserHistoryPagedGetRequest;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TraktUserHistoryScheduledRunner implements TraktPagedGetRunner<UserHistory, UserHistoryPagedGetRequest>{
	@Getter
	private final JDA jda;
	
	public TraktUserHistoryScheduledRunner(JDA jda){this.jda = jda;}
	
	@Override
	public void run(){
		this.runQueryOnDefaultUsersChannels();
	}
	
	@Override
	public Set<TextChannel> getChannels(){
		return this.getJda().getGuilds().stream().map(g -> Settings.get(g).getTraktConfiguration().getMediaChangeChannel().map(ChannelConfiguration::getChannel).filter(Optional::isPresent).map(Optional::get).orElse(null)).filter(Objects::nonNull).collect(Collectors.toSet());
	}
	
	@Override
	public @NonNull String getRunnerName(){
		return "history";
	}
	
	@NonNull
	@Override
	public UserHistoryPagedGetRequest initQuery(@NonNull Member member){
		return new UserHistoryPagedGetRequest(TraktUtils.getUsername(member).orElseThrow(() -> new RuntimeException("Failed to get username for member " + member)), 1, Settings.get(member.getGuild()).getTraktConfiguration().getLastAccess(getRunnerName(), member.getIdLong()).map(UserDateConfiguration::getDate).orElse(null));
	}
	
	@Override
	public boolean isKeepOnlyNew(){
		return true;
	}
	
	@Override
	public long getDelay(){
		return 7;
	}
	
	@Override
	public long getPeriod(){
		return 30;
	}
	
	@Override
	public @NonNull TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
	
	@Override
	public void buildMessage(EmbedBuilder builder, User user, @NonNull UserHistory change){
		TraktPagedGetRunner.super.buildMessage(builder, user, change);
		Optional<URL> poster = Optional.empty();
		if(change instanceof UserMovieHistory){
			poster = Optional.ofNullable(((UserMovieHistory) change).getMovie().getIds().getTmdb()).map(MovieDetailsGetRequest::new).map(query -> {
				try{
					return TMDBUtils.getQuery(query);
				}
				catch(RequestException | URISyntaxException | MalformedURLException e){
					Log.getLogger(null).error("Failed to get poster for change {}", change, e);
				}
				return null;
			}).flatMap(MovieDetails::getPosterURL);
		}
		else if(change instanceof UserSerieHistory){
			poster = Optional.ofNullable(((UserSerieHistory) change).getShow().getIds().getTmdb()).map(TVDetailsGetRequest::new).map(query -> {
				try{
					return TMDBUtils.getQuery(query);
				}
				catch(RequestException | URISyntaxException | MalformedURLException e){
					Log.getLogger(null).error("Failed to get poster for change {}", change, e);
				}
				return null;
			}).flatMap(TVDetails::getPosterURL);
		}
		poster.ifPresent(posterUrl -> builder.setThumbnail(posterUrl.toString()));
	}
}
