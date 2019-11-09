package fr.raksrinana.rsndiscord.runners.anilist;

import fr.raksrinana.rsndiscord.runners.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.anilist.AniListUtils;
import fr.raksrinana.rsndiscord.utils.anilist.notifications.Notification;
import fr.raksrinana.rsndiscord.utils.anilist.queries.NotificationsPagedQuery;
import fr.raksrinana.rsndiscord.utils.log.Log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListNotificationScheduledRunner implements AniListRunner<Notification, NotificationsPagedQuery>, ScheduledRunner{
	private final JDA jda;
	
	public AniListNotificationScheduledRunner(@Nonnull final JDA jda){
		Log.getLogger(null).info("Creating AniList {} runner", this.getRunnerName());
		this.jda = jda;
	}
	
	@Override
	public void run(){
		this.runQueryOnEveryUserAndDefaultChannels();
	}
	
	@Override
	public List<TextChannel> getChannels(){
		return this.getJDA().getGuilds().stream().map(g -> Settings.getConfiguration(g).getAniListConfiguration().getNotificationsChannel().map(ChannelConfiguration::getChannel).filter(Optional::isPresent).map(Optional::get).orElse(null)).filter(Objects::nonNull).collect(Collectors.toList());
	}
	
	@Override
	public long getDelay(){
		return 1;
	}
	
	@Override
	public void sendMessages(@Nonnull final List<TextChannel> channels, @Nonnull final Map<User, List<Notification>> userElements){
		final var notifications = new HashMap<Notification, List<User>>();
		for(final var entry : userElements.entrySet()){
			for(final var notification : entry.getValue()){
				notifications.putIfAbsent(notification, new LinkedList<>());
				notifications.get(notification).add(entry.getKey());
			}
		}
		notifications.entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().getDate())).forEachOrdered(e -> channels.forEach(channel -> {
			final var mentions = e.getValue().stream().filter(u -> this.sendToChannel(channel, u)).distinct().map(User::getAsMention).collect(Collectors.toList());
			if(!mentions.isEmpty()){
				Actions.sendMessage(channel, String.join("\n", mentions));
				Actions.sendMessage(channel, this.buildMessage(null, e.getKey()));
			}
		}));
	}
	
	@Nonnull
	@Override
	public NotificationsPagedQuery initQuery(@Nonnull final Member member){
		return new NotificationsPagedQuery(AniListUtils.getUserId(member).orElseThrow(), Settings.getConfiguration(member.getGuild()).getAniListConfiguration().getLastAccess(this.getFetcherID()).stream().filter(c -> Objects.equals(c.getUserId(), member.getUser().getIdLong())).map(UserDateConfiguration::getDate).findAny().orElse(AniListUtils.getDefaultDate(member)));
	}
	
	@Override
	public boolean keepOnlyNew(){
		return true;
	}
	
	@Override
	public long getPeriod(){
		return 15;
	}
	
	@Nonnull
	@Override
	public String getFetcherID(){
		return "notification";
	}
	
	@Nonnull
	@Override
	public String getRunnerName(){
		return "notification";
	}
	
	@Nonnull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
	
	@Nonnull
	@Override
	public JDA getJDA(){
		return this.jda;
	}
}
