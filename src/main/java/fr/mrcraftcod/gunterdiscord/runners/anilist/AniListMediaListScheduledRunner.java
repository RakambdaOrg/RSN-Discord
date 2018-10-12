package fr.mrcraftcod.gunterdiscord.runners.anilist;

import fr.mrcraftcod.gunterdiscord.utils.anilist.list.AniListMediaList;
import fr.mrcraftcod.gunterdiscord.utils.anilist.queries.AniListMediaListPagedQuery;
import net.dv8tion.jda.core.JDA;
import java.util.Map;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListMediaListScheduledRunner implements AniListRunner<AniListMediaList, AniListMediaListPagedQuery>{
	private final JDA jda;
	
	public AniListMediaListScheduledRunner(final JDA jda){
		getLogger(null).info("Creating AniList {} runner", getRunnerName());
		this.jda = jda;
	}
	
	@Override
	public String getRunnerName(){
		return "media list";
	}
	
	@Override
	public JDA getJDA(){
		return this.jda;
	}
	
	@Override
	public AniListMediaListPagedQuery initQuery(final Map<String, String> userInfo){
		return new AniListMediaListPagedQuery(Integer.parseInt(userInfo.get("userId")));
	}
	
	@Override
	public boolean keepOnlyNew(){
		return true;
	}
	
	@Override
	public String getFetcherID(){
		return "medialist";
	}
}
