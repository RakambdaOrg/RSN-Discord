package fr.mrcraftcod.gunterdiscord.runners.anilist;

import fr.mrcraftcod.gunterdiscord.runners.ScheduledRunner;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.anilist.notifications.airing.AniListAiringNotification;
import fr.mrcraftcod.gunterdiscord.utils.anilist.queries.AniListNotificationsPagedQuery;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListNotificationScheduledRunner implements AniListRunner<AniListAiringNotification, AniListNotificationsPagedQuery>, ScheduledRunner{
	private final JDA jda;
	
	public AniListNotificationScheduledRunner(final JDA jda){
		getLogger(null).info("Creating AniList {} runner", getRunnerName());
		this.jda = jda;
	}
	
	@Override
	public void run(){
		runQueryOnEveryUserAndDefaultChannels();
	}
	
	@Override
	public long getPeriod(){
		return 15;
	}
	
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
	
	@Override
	public long getDelay(){
		return 1;
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
		final var notifications = new HashMap<AniListAiringNotification, List<User>>();
		for(final var user : userElements.keySet()){
			for(final var notification : userElements.get(user)){
				notifications.putIfAbsent(notification, new LinkedList<>());
				notifications.get(notification).add(user);
			}
		}
		notifications.entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().getDate())).forEachOrdered(e -> channels.forEach(c -> {
			final var mentions = e.getValue().stream().filter(u -> sendToChannel(c, u)).map(User::getAsMention).collect(Collectors.toList());
			if(!mentions.isEmpty()){
				Actions.sendMessage(c, String.join("\n", mentions));
				Actions.sendMessage(c, buildMessage(null, e.getKey()));
			}
		}));
	}
}
