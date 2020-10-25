package fr.raksrinana.rsndiscord.modules.anilist.runner;

import fr.raksrinana.rsndiscord.modules.anilist.data.notifications.INotification;
import fr.raksrinana.rsndiscord.modules.anilist.query.NotificationsPagedQuery;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
				.map(g -> Settings.get(g).getAniListConfiguration()
						.getNotificationsChannel()
						.map(ChannelConfiguration::getChannel)
						.filter(Optional::isPresent)
						.map(Optional::get)
						.orElse(null))
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}
	
	@Override
	public void sendMessages(@NonNull final Set<TextChannel> channels, @NonNull final Map<User, Set<INotification>> userElements){
		final var notifications = new HashMap<INotification, List<User>>();
		for(final var entry : userElements.entrySet()){
			for(final var notification : entry.getValue()){
				notifications.putIfAbsent(notification, new LinkedList<>());
				notifications.get(notification).add(entry.getKey());
			}
		}
		notifications.entrySet().stream()
				.sorted(Comparator.comparing(e -> e.getKey().getDate()))
				.forEachOrdered(e -> channels.forEach(channel -> {
					final var mentions = e.getValue().stream()
							.filter(u -> this.sendToChannel(channel, u))
							.distinct()
							.map(User::getAsMention)
							.collect(Collectors.toList());
					if(!mentions.isEmpty()){
						Actions.sendMessage(channel, String.join("\n", mentions), this.buildMessage(channel.getGuild(), null, e.getKey()));
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
		return TimeUnit.MINUTES;
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
