package fr.raksrinana.rsndiscord.modules.anilist.runner;

import fr.raksrinana.rsndiscord.modules.anilist.data.notifications.INotification;
import fr.raksrinana.rsndiscord.modules.anilist.query.NotificationsPagedQuery;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@ScheduledRunner
public class AniListNotificationRunner implements IAniListRunner<INotification, NotificationsPagedQuery>{
	@Getter
	private final JDA jda;
	
	public AniListNotificationRunner(@NonNull final JDA jda){
		this.jda = jda;
	}
	
	@Override
	public Set<TextChannel> getChannels(){
		return this.getJda().getGuilds().stream()
				.flatMap(g -> Settings.get(g).getAniListConfiguration()
						.getNotificationsChannel()
						.map(ChannelConfiguration::getChannel)
						.map(Optional::get)
						.stream())
				.filter(Objects::nonNull)
				.collect(toSet());
	}
	
	@Override
	public void sendMessages(@NonNull final Set<TextChannel> channels, @NonNull final Map<User, Set<INotification>> userElements){
		var notifications = new HashMap<INotification, List<User>>();
		for(var entry : userElements.entrySet()){
			for(var notification : entry.getValue()){
				notifications.putIfAbsent(notification, new LinkedList<>());
				notifications.get(notification).add(entry.getKey());
			}
		}
		notifications.entrySet().stream()
				.sorted(comparing(e -> e.getKey().getDate()))
				.forEachOrdered(e -> channels.forEach(channel -> {
					var mentions = e.getValue().stream()
							.filter(user -> this.shouldSendTo(channel, user))
							.distinct()
							.map(User::getAsMention)
							.collect(toList());
					if(!mentions.isEmpty()){
						channel.sendMessage(String.join("\n", mentions))
								.embed(this.buildMessage(channel.getGuild(), null, e.getKey()))
								.submit();
					}
				}));
	}
	
	@NonNull
	@Override
	public NotificationsPagedQuery initQuery(@NonNull final Member member){
		return new NotificationsPagedQuery();
	}
	
	@Override
	public boolean isKeepOnlyNew(){
		return true;
	}
	
	@NonNull
	@Override
	public String getFetcherID(){
		return "notification";
	}
	
	@Override
	public void execute(){
		this.runQueryOnDefaultUsersChannels();
	}
	
	@Override
	public long getDelay(){
		return 1;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Notification";
	}
	
	@Override
	public long getPeriod(){
		return 15;
	}
}
