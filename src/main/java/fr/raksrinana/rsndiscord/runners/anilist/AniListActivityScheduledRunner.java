package fr.raksrinana.rsndiscord.runners.anilist;

import fr.raksrinana.rsndiscord.runners.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import fr.raksrinana.rsndiscord.utils.anilist.AniListUtils;
import fr.raksrinana.rsndiscord.utils.anilist.activity.list.ListActivity;
import fr.raksrinana.rsndiscord.utils.anilist.queries.ActivityPagedQuery;
import fr.raksrinana.rsndiscord.utils.log.Log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListActivityScheduledRunner implements AniListRunner<ListActivity, ActivityPagedQuery>, ScheduledRunner{
	private final JDA jda;
	
	public AniListActivityScheduledRunner(@Nonnull final JDA jda){
		Log.getLogger(null).info("Creating AniList {} runner", this.getRunnerName());
		this.jda = jda;
	}
	
	@Override
	public void run(){
		this.runQueryOnEveryUserAndDefaultChannels();
	}
	
	@Override
	public List<TextChannel> getChannels(){
		return this.getJDA().getGuilds().stream().map(g -> Settings.getConfiguration(g).getAniListConfiguration().getMediaChangeChannel().map(ChannelConfiguration::getChannel).filter(Optional::isPresent).map(Optional::get).orElse(null)).filter(Objects::nonNull).collect(Collectors.toList());
	}
	
	@Nonnull
	@Override
	public String getRunnerName(){
		return "list activity";
	}
	
	@Nonnull
	@Override
	public ActivityPagedQuery initQuery(@Nonnull final Member member){
		return new ActivityPagedQuery(AniListUtils.getUserId(member).orElseThrow(), Settings.getConfiguration(member.getGuild()).getAniListConfiguration().getLastAccess(this.getFetcherID()).stream().filter(a -> Objects.equals(a.getUserId(), member.getUser().getIdLong())).map(UserDateConfiguration::getDate).findAny().orElse(AniListUtils.getDefaultDate(member)));
	}
	
	@Override
	public boolean keepOnlyNew(){
		return true;
	}
	
	@Nonnull
	@Override
	public JDA getJDA(){
		return this.jda;
	}
	
	@Nonnull
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
	
	@Nonnull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
}
