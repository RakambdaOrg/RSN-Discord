package fr.mrcraftcod.gunterdiscord.runners.anilist;

import fr.mrcraftcod.gunterdiscord.utils.anilist.activity.list.AniListListActivity;
import fr.mrcraftcod.gunterdiscord.utils.anilist.queries.AniListListActivityPagedQuery;
import net.dv8tion.jda.core.JDA;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListActivityScheduledRunner implements AniListRunner<AniListListActivity, AniListListActivityPagedQuery>{
	private final JDA jda;
	
	public AniListActivityScheduledRunner(final JDA jda){
		getLogger(null).info("Creating AniList list change runner");
		this.jda = jda;
	}
	
	@Override
	public long getPeriod(){
		return 1;
	}
	
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@Override
	public String getRunnerName(){
		return "list activity";
	}
	
	@Override
	public JDA getJDA(){
		return this.jda;
	}
	
	@Override
	public AniListListActivityPagedQuery initQuery(final Map<String, String> userInfo){
		return new AniListListActivityPagedQuery(Integer.parseInt(userInfo.get("userId")), Integer.parseInt(userInfo.get("lastFetch" + getFetcherID())));
	}
	
	@Override
	public boolean keepOnlyNew(){
		return true;
	}
	
	@Override
	public String getFetcherID(){
		return "listActivity";
	}
}
