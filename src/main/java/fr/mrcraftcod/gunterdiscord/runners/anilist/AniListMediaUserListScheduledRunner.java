package fr.mrcraftcod.gunterdiscord.runners.anilist;

import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.UserConfiguration;
import fr.mrcraftcod.gunterdiscord.runners.ScheduledRunner;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListUtils;
import fr.mrcraftcod.gunterdiscord.utils.anilist.list.AniListMediaUserList;
import fr.mrcraftcod.gunterdiscord.utils.anilist.queries.AniListMediaUserListPagedQuery;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.tuple.ImmutablePair;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
@SuppressWarnings("WeakerAccess")
public class AniListMediaUserListScheduledRunner implements AniListRunner<AniListMediaUserList, AniListMediaUserListPagedQuery>, ScheduledRunner{
	private final JDA jda;
	private final boolean keepOnlyNew;
	private boolean sortedByUser;
	
	public AniListMediaUserListScheduledRunner(@Nonnull final JDA jda){
		this(jda, true);
	}
	
	public AniListMediaUserListScheduledRunner(@Nonnull final JDA jda, final boolean keepOnlyNew){
		getLogger(null).info("Creating AniList {} runner", getRunnerName());
		this.jda = jda;
		this.keepOnlyNew = keepOnlyNew;
		this.sortedByUser = false;
	}
	
	@Override
	public boolean sortedByUser(){
		return this.sortedByUser;
	}
	
	@Override
	public long getPeriod(){
		return 1;
	}
	
	@Override
	public void sendMessages(@Nonnull final List<TextChannel> channels, @Nonnull final Map<User, List<AniListMediaUserList>> userElements){
		AniListRunner.super.sendMessages(channels, userElements);
		this.getJDA().getGuilds().stream().map(g -> NewSettings.getConfiguration(g).getAniListConfiguration().getThaChannel().flatMap(ChannelConfiguration::getChannel)).filter(Optional::isPresent).map(Optional::get).forEach(textChannel -> {
			NewSettings.getConfiguration(textChannel.getGuild()).getAniListConfiguration().getThaUser().flatMap(UserConfiguration::getUser).ifPresent(user -> {
				userElements.entrySet().stream().flatMap(e -> e.getValue().stream().map(v -> ImmutablePair.of(e.getKey(), v))).filter(v -> v.getRight().getCustomLists().entrySet().stream().filter(Map.Entry::getValue).anyMatch(entry -> Objects.equals("ThaPending", entry.getKey()) || Objects.equals("ThaReading", entry.getKey()) || Objects.equals("ThaWatching", entry.getKey()))).forEach(p -> Actions.sendMessage(textChannel, "" + user.getAsMention(), buildMessage(p.getLeft(), p.getRight())));
			});
		});
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@Nonnull
	@Override
	public String getRunnerName(){
		return "media list";
	}
	
	@Nonnull
	@Override
	public JDA getJDA(){
		return this.jda;
	}
	
	@Nonnull
	@Override
	public AniListMediaUserListPagedQuery initQuery(@Nonnull Member member){
		return new AniListMediaUserListPagedQuery(AniListUtils.getUserId(member).orElseThrow());
	}
	
	@Override
	public boolean keepOnlyNew(){
		return this.keepOnlyNew;
	}
	
	@Nonnull
	@SuppressWarnings("SpellCheckingInspection")
	@Override
	public String getFetcherID(){
		return "medialist";
	}
	
	@Override
	public void run(){
		runQueryOnEveryUserAndDefaultChannels();
	}
	
	@Nonnull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
}
