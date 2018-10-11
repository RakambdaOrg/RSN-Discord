package fr.mrcraftcod.gunterdiscord.runners.anilist;

import fr.mrcraftcod.gunterdiscord.utils.anilist.notifications.airing.AniListAiringNotification;
import fr.mrcraftcod.gunterdiscord.utils.anilist.queries.AniListNotificationsPagedQuery;
import net.dv8tion.jda.core.JDA;
import java.util.Map;
import java.util.Optional;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListNotificationScheduledRunner implements AniListRunner<AniListAiringNotification, AniListNotificationsPagedQuery>{
	private final JDA jda;
	
	public AniListNotificationScheduledRunner(final JDA jda){
		getLogger(null).info("Creating AniList {} runner", getRunnerName());
		this.jda = jda;
	}
	
	@Override
	public String getRunnerName(){
		return "notification";
	}
	
	@Override
	public JDA getJDA(){
		return this.jda;
	}
	
	@Override
	public AniListNotificationsPagedQuery initQuery(final Map<String, String> userInfo){
		return new AniListNotificationsPagedQuery(Integer.parseInt(userInfo.get("userId")), Optional.ofNullable(userInfo.getOrDefault("lastFetch" + getFetcherID(), null)).map(Integer::parseInt).orElse(0));
	}
	
	@Override
	public boolean keepOnlyNew(){
		return true;
	}
	
	@Override
	public String getFetcherID(){
		return "notification";
	}
}
