package fr.raksrinana.rsndiscord.runner.impl.anilist;

import fr.raksrinana.rsndiscord.api.anilist.data.notifications.Notification;
import fr.raksrinana.rsndiscord.api.anilist.query.NotificationsPagedQuery;
import fr.raksrinana.rsndiscord.runner.api.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@ScheduledRunner
public class AniListNotificationRunner extends IAniListRunner<Notification, NotificationsPagedQuery>{
	@NotNull
	@Override
	public String getName(){
		return "Notification";
	}
	
	@NotNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
	
	@Override
	public boolean isKeepOnlyNew(){
		return true;
	}
	
	@Override
	@NotNull
	public Set<TextChannel> getChannels(@NotNull JDA jda){
		return jda.getGuilds().stream()
				.flatMap(g -> Settings.get(g).getAniListConfiguration()
						.getNotificationsChannel()
						.map(ChannelConfiguration::getChannel)
						.filter(Optional::isPresent)
						.map(Optional::get)
						.stream())
				.filter(Objects::nonNull)
				.collect(toSet());
	}
	
	@NotNull
	@Override
	public String getFetcherID(){
		return "notification";
	}
	
	@Override
	public long getDelay(){
		return 1;
	}
	
	@Override
	public void sendMessages(@NotNull JDA jda, @NotNull Set<TextChannel> channels, @NotNull Map<User, Set<Notification>> userElements){
		var notifications = new HashMap<Notification, List<User>>();
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
							.filter(user -> shouldSendTo(channel, user))
							.distinct()
							.map(User::getAsMention)
							.collect(toList());
					if(!mentions.isEmpty()){
						JDAWrappers.message(channel, String.join("\n", mentions))
								.embed(buildMessage(channel.getGuild(), null, e.getKey()))
								.submit();
					}
				}));
	}
	
	@NotNull
	@Override
	public NotificationsPagedQuery initQuery(@NotNull Member member){
		return new NotificationsPagedQuery();
	}
	
	@Override
	public long getPeriod(){
		return 15;
	}
}
