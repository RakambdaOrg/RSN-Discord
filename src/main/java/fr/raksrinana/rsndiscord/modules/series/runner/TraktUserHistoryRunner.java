package fr.raksrinana.rsndiscord.modules.series.runner;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.series.themoviedb.TMDBUtils;
import fr.raksrinana.rsndiscord.modules.series.themoviedb.model.MediaDetails;
import fr.raksrinana.rsndiscord.modules.series.themoviedb.requests.ITMDBGetRequest;
import fr.raksrinana.rsndiscord.modules.series.themoviedb.requests.MovieDetailsGetRequest;
import fr.raksrinana.rsndiscord.modules.series.themoviedb.requests.TVDetailsGetRequest;
import fr.raksrinana.rsndiscord.modules.series.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.modules.series.trakt.model.users.history.UserHistory;
import fr.raksrinana.rsndiscord.modules.series.trakt.model.users.history.UserMovieHistory;
import fr.raksrinana.rsndiscord.modules.series.trakt.requests.users.UserHistoryPagedGetRequest;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.types.UserDateConfiguration;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import fr.raksrinana.rsndiscord.utils.RequestException;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@ScheduledRunner
public class TraktUserHistoryRunner implements ITraktPagedGetRunner<UserHistory, UserHistoryPagedGetRequest>{
	@Getter
	private final JDA jda;
	
	public TraktUserHistoryRunner(JDA jda){this.jda = jda;}
	
	@Override
	public Set<TextChannel> getChannels(){
		return this.getJda().getGuilds().stream().map(g -> Settings.get(g).getTraktConfiguration().getMediaChangeChannel().map(ChannelConfiguration::getChannel).filter(Optional::isPresent).map(Optional::get).orElse(null)).filter(Objects::nonNull).collect(Collectors.toSet());
	}
	
	@NonNull
	@Override
	public UserHistoryPagedGetRequest initQuery(@NonNull Member member){
		return new UserHistoryPagedGetRequest(TraktUtils.getUsername(member).orElseThrow(() -> new RuntimeException("Failed to get username for member " + member)),
				1,
				Settings.getGeneral().getTrakt()
						.getLastAccess(getFetcherID(), member.getIdLong())
						.map(UserDateConfiguration::getDate)
						.map(date -> date.plusNanos(1000L))
						.orElse(ZonedDateTime.now()));
	}
	
	@Override
	public boolean isKeepOnlyNew(){
		return true;
	}
	
	@NonNull
	private static Optional<MediaDetails> getTMDBInfos(@NonNull UserHistory change){
		final Function<Long, ITMDBGetRequest<? extends MediaDetails>> requestBuilder = change instanceof UserMovieHistory ? MovieDetailsGetRequest::new : TVDetailsGetRequest::new;
		return Optional.ofNullable(change.getIds().getTmdb()).map(requestBuilder).map(query -> {
			try{
				return TMDBUtils.getQuery(query);
			}
			catch(RequestException e){
				Log.getLogger(null).error("Failed to get extra movie infos for {}", change, e);
			}
			return null;
		});
	}
	
	@Override
	public void buildMessage(@NonNull Guild guild, EmbedBuilder builder, User user, @NonNull UserHistory change){
		if(Objects.isNull(user)){
			builder.setAuthor(this.getJda().getSelfUser().getName(), null, this.getJda().getSelfUser().getAvatarUrl());
		}
		else{
			builder.setAuthor(user.getName(), null, user.getAvatarUrl());
		}
		getTMDBInfos(change).ifPresentOrElse(mediaDetails -> change.fillEmbed(guild, builder, mediaDetails), () -> change.fillEmbed(guild, builder));
	}
	
	@Override
	public void execute(){
		this.runQueryOnDefaultUsersChannels();
	}
	
	@Override
	public long getDelay(){
		return 7;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Trakt user history";
	}
	
	@Override
	public long getPeriod(){
		return 1;
	}
	
	@Override
	public @NonNull TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
	
	@Override
	public String getFetcherID(){
		return "history";
	}
}