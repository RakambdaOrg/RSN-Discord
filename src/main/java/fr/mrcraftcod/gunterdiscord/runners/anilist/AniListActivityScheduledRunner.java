package fr.mrcraftcod.gunterdiscord.runners.anilist;

import fr.mrcraftcod.gunterdiscord.runners.ScheduledRunner;
import fr.mrcraftcod.gunterdiscord.utils.anilist.activity.list.AniListListActivity;
import fr.mrcraftcod.gunterdiscord.utils.anilist.queries.AniListListActivityPagedQuery;
import net.dv8tion.jda.api.JDA;
import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListActivityScheduledRunner implements AniListRunner<AniListListActivity, AniListListActivityPagedQuery>, ScheduledRunner{
	private final JDA jda;
	
	public AniListActivityScheduledRunner(@Nonnull final JDA jda){
		getLogger(null).info("Creating AniList list change runner");
		this.jda = jda;
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
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@Nonnull
	@Override
	public String getRunnerName(){
		return "list activity";
	}
	
	@Nonnull
	@Override
	public JDA getJDA(){
		return this.jda;
	}
	
	@Nonnull
	@Override
	public AniListListActivityPagedQuery initQuery(@Nonnull final Map<String, String> userInfo){
		return new AniListListActivityPagedQuery(Integer.parseInt(userInfo.get("userId")), Integer.parseInt(userInfo.get("lastFetch" + getFetcherID())));
	}
	
	@Override
	public boolean keepOnlyNew(){
		return true;
	}
	
	@Nonnull
	@Override
	public String getFetcherID(){
		return "listActivity";
	}
	
	@Override
	public void run(){
		runQueryOnEveryUserAndDefaultChannels();
	}
}
