package fr.mrcraftcod.gunterdiscord.runners.anilist;

import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListDatedObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.notifications.airing.AniListAiringNotification;
import fr.mrcraftcod.gunterdiscord.utils.anilist.queries.AniListNotificationsPagedQuery;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
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
	
	@Override
	public void sendMessages(final List<TextChannel> channels, final Map<User, List<AniListAiringNotification>> userElements){
		userElements.values().stream().flatMap(List::stream).distinct().sorted(Comparator.comparing(AniListDatedObject::getDate)).map(change -> buildMessage(null, change)).<Consumer<? super TextChannel>> map(message -> c -> Actions.sendMessage(c, message)).forEach(channels::forEach);
	}
}
