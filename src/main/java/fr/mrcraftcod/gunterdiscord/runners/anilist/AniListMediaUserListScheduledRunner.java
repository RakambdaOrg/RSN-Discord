package fr.mrcraftcod.gunterdiscord.runners.anilist;

import fr.mrcraftcod.gunterdiscord.runners.ScheduledRunner;
import fr.mrcraftcod.gunterdiscord.settings.configs.AnilistThaChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.AnilistThaUserConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.anilist.list.AniListMediaUserList;
import fr.mrcraftcod.gunterdiscord.utils.anilist.queries.AniListMediaUserListPagedQuery;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.tuple.ImmutablePair;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
	
	public AniListMediaUserListScheduledRunner(final JDA jda){
		this(jda, true);
	}
	
	public AniListMediaUserListScheduledRunner(final JDA jda, boolean keepOnlyNew){
		getLogger(null).info("Creating AniList {} runner", getRunnerName());
		this.jda = jda;
		this.keepOnlyNew = keepOnlyNew;
		this.sortedByUser = false;
	}
	
	public AniListMediaUserListScheduledRunner sortByUser(){
		this.sortedByUser = true;
		return this;
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
	public TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
	
	@Override
	public long getDelay(){
		return 0;
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
	public AniListMediaUserListPagedQuery initQuery(final Map<String, String> userInfo){
		return new AniListMediaUserListPagedQuery(Integer.parseInt(userInfo.get("userId")));
	}
	
	@Override
	public boolean keepOnlyNew(){
		return this.keepOnlyNew;
	}
	
	@SuppressWarnings("SpellCheckingInspection")
	@Override
	public String getFetcherID(){
		return "medialist";
	}
	
	@Override
	public void run(){
		runQueryOnEveryUserAndDefaultChannels();
	}
	
	@Override
	public void sendMessages(List<TextChannel> channels, Map<User, List<AniListMediaUserList>> userElements){
		AniListRunner.super.sendMessages(channels, userElements);
		this.getJDA().getGuilds().stream().map(g -> new AnilistThaChannelConfig(g).getObject(null)).filter(Objects::nonNull).forEach(textChannel -> {
			final var member = new AnilistThaUserConfig(textChannel.getGuild()).getObject(null);
			if(Objects.nonNull(member)){
				userElements.entrySet().stream().flatMap(e -> e.getValue().stream().map(v -> ImmutablePair.of(e.getKey(), v))).filter(v -> v.getRight().getCustomLists().entrySet().stream().filter(Map.Entry::getValue).anyMatch(c -> Objects.equals("ThaPending", c.getKey()) || Objects.equals("ThaReading", c.getKey()) || Objects.equals("ThaWatching", c.getKey()))).forEach(p -> Actions.sendMessage(textChannel, "" + member.getAsMention(), buildMessage(p.getLeft(), p.getRight())));
			}
		});
	}
}
