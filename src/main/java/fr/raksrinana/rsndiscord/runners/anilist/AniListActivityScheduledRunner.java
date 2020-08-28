package fr.raksrinana.rsndiscord.runners.anilist;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import fr.raksrinana.rsndiscord.utils.anilist.AniListUtils;
import fr.raksrinana.rsndiscord.utils.anilist.activity.list.ListActivity;
import fr.raksrinana.rsndiscord.utils.anilist.queries.ActivityPagedQuery;
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

public class AniListActivityScheduledRunner implements AniListRunner<ListActivity, ActivityPagedQuery>{
	@Getter
	private final JDA jda;
	
	public AniListActivityScheduledRunner(@NonNull final JDA jda){
		this.jda = jda;
	}
	
	@Override
	public Set<TextChannel> getChannels(){
		return this.getJda().getGuilds().stream().map(g -> Settings.get(g).getAniListConfiguration().getMediaChangeChannel().map(ChannelConfiguration::getChannel).filter(Optional::isPresent).map(Optional::get).orElse(null)).filter(Objects::nonNull).collect(Collectors.toSet());
	}
	
	@NonNull
	@Override
	public ActivityPagedQuery initQuery(@NonNull final Member member){
		return new ActivityPagedQuery(AniListUtils.getUserId(member).orElseThrow(),
				Settings.getGeneral().getAniList().getLastAccess(this.getFetcherID(), member.getIdLong())
						.map(UserDateConfiguration::getDate)
						.orElse(AniListUtils.getDefaultDate()));
	}
	
	@Override
	public boolean isKeepOnlyNew(){
		return true;
	}
	
	@NonNull
	@Override
	public String getFetcherID(){
		return "listActivity";
	}
	
	@Override
	public long getDelay(){
		return 6;
	}
	
	@Override
	public long getPeriod(){
		return 1;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
	
	@Override
	public void execute(){
		this.runQueryOnDefaultUsersChannels();
	}
	
	@NonNull
	@Override
	public String getName(){
		return "AniList list activity";
	}
}
