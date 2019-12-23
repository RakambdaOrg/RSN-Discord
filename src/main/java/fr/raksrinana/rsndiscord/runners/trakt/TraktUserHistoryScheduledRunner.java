package fr.raksrinana.rsndiscord.runners.trakt;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import fr.raksrinana.rsndiscord.utils.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.utils.trakt.model.users.history.UserHistory;
import fr.raksrinana.rsndiscord.utils.trakt.requests.users.UserHistoryPagedGetRequest;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
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
		return this.getJda().getGuilds().stream().map(g -> Settings.get(g).getAniListConfiguration().getNotificationsChannel().map(ChannelConfiguration::getChannel).filter(Optional::isPresent).map(Optional::get).orElse(null)).filter(Objects::nonNull).collect(Collectors.toSet());
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
}
