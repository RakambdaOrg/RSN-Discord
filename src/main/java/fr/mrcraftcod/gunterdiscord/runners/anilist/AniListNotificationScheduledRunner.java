package fr.mrcraftcod.gunterdiscord.runners.anilist;

import fr.mrcraftcod.gunterdiscord.runners.ScheduledRunner;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.UserDateConfiguration;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListUtils;
import fr.mrcraftcod.gunterdiscord.utils.anilist.notifications.airing.AniListAiringNotification;
import fr.mrcraftcod.gunterdiscord.utils.anilist.queries.AniListNotificationsPagedQuery;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;
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
	
	public AniListNotificationScheduledRunner(@Nonnull final JDA jda){
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
	public void sendMessages(@Nonnull final List<TextChannel> channels, @Nonnull final Map<User, List<AniListAiringNotification>> userElements){
		final var notifications = new HashMap<AniListAiringNotification, List<User>>();
		for(final var user : userElements.keySet()){
			for(final var notification : userElements.get(user)){
				notifications.putIfAbsent(notification, new LinkedList<>());
				notifications.get(notification).add(user);
			}
		}
		notifications.entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().getDate())).forEachOrdered(e -> channels.forEach(channel -> {
			final var mentions = e.getValue().stream().filter(u -> sendToChannel(channel, u)).distinct().map(User::getAsMention).collect(Collectors.toList());
			if(!mentions.isEmpty()){
				Actions.sendMessage(channel, String.join("\n", mentions));
				Actions.sendMessage(channel, buildMessage(null, e.getKey()));
			}
		}));
	}
	
	@Override
	public long getDelay(){
		return 1;
	}
	
	@Override
	public List<TextChannel> getChannels(){
		return getJDA().getGuilds().stream().map(g -> NewSettings.getConfiguration(g).getAniListConfiguration().getNotificationsChannel().map(ChannelConfiguration::getChannel).filter(Optional::isPresent).map(Optional::get).orElse(null)).filter(Objects::nonNull).collect(Collectors.toList());
	}
	
	@Nonnull
	@Override
	public String getRunnerName(){
		return "notification";
	}
	
	@Nonnull
	@Override
	public JDA getJDA(){
		return this.jda;
	}
	
	@Nonnull
	@Override
	public AniListNotificationsPagedQuery initQuery(@Nonnull Member member){
		return new AniListNotificationsPagedQuery(AniListUtils.getUserId(member).orElseThrow(), NewSettings.getConfiguration(member.getGuild()).getAniListConfiguration().getLastAccess(getFetcherID()).stream().filter(c -> Objects.equals(c.getUserId(), member.getUser().getIdLong())).map(UserDateConfiguration::getDate).findAny().orElse(AniListUtils.getDefaultDate(member)));
	}
	
	@Override
	public boolean keepOnlyNew(){
		return true;
	}
	
	@Nonnull
	@Override
	public String getFetcherID(){
		return "notification";
	}
	
	@Nonnull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
}
