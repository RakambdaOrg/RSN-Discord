package fr.raksrinana.rsndiscord.runners.anilist;

import fr.raksrinana.rsndiscord.runners.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.NewSettings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.TodoConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.anilist.AniListUtils;
import fr.raksrinana.rsndiscord.utils.anilist.list.AniListMediaUserList;
import fr.raksrinana.rsndiscord.utils.anilist.queries.AniListMediaUserListPagedQuery;
import fr.raksrinana.rsndiscord.utils.log.Log;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.tuple.ImmutablePair;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListMediaUserListScheduledRunner implements AniListRunner<AniListMediaUserList, AniListMediaUserListPagedQuery>, ScheduledRunner{
	private final JDA jda;
	private final boolean keepOnlyNew;
	private final boolean sortedByUser;
	
	public AniListMediaUserListScheduledRunner(@Nonnull final JDA jda){
		this(jda, true);
	}
	
	private AniListMediaUserListScheduledRunner(@Nonnull final JDA jda, final boolean keepOnlyNew){
		Log.getLogger(null).info("Creating AniList {} runner", this.getRunnerName());
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
	public void run(){
		this.runQueryOnEveryUserAndDefaultChannels();
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@Override
	public List<TextChannel> getChannels(){
		return this.getJDA().getGuilds().stream().map(g -> NewSettings.getConfiguration(g).getAniListConfiguration().getMediaChangeChannel().map(ChannelConfiguration::getChannel).filter(Optional::isPresent).map(Optional::get).orElse(null)).filter(Objects::nonNull).collect(Collectors.toList());
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
	
	@Override
	public void sendMessages(@Nonnull final List<TextChannel> channels, @Nonnull final Map<User, List<AniListMediaUserList>> userElements){
		AniListRunner.super.sendMessages(channels, userElements);
		this.getJDA().getGuilds().stream().map(g -> NewSettings.getConfiguration(g).getAniListConfiguration().getThaChannel().flatMap(ChannelConfiguration::getChannel)).filter(Optional::isPresent).map(Optional::get).forEach(textChannel -> NewSettings.getConfiguration(textChannel.getGuild()).getAniListConfiguration().getThaUser().flatMap(UserConfiguration::getUser).ifPresent(user -> userElements.entrySet().stream().flatMap(e -> e.getValue().stream().map(v -> ImmutablePair.of(e.getKey(), v))).filter(v -> v.getRight().getCustomLists().entrySet().stream().filter(Map.Entry::getValue).anyMatch(entry -> Objects.equals("ThaPending", entry.getKey()) || Objects.equals("ThaReading", entry.getKey()) || Objects.equals("ThaWatching", entry.getKey()))).forEach(p -> Actions.sendMessage(textChannel, user.getAsMention(), this.buildMessage(p.getLeft(), p.getRight()), sentMessage -> {
			sentMessage.addReaction(BasicEmotes.CHECK_OK.getValue()).queue();
			NewSettings.getConfiguration(textChannel.getGuild()).addTodoMessage(new TodoConfiguration(sentMessage, true));
		}))));
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
	
	@Nonnull
	@Override
	public AniListMediaUserListPagedQuery initQuery(@Nonnull final Member member){
		return new AniListMediaUserListPagedQuery(AniListUtils.getUserId(member).orElseThrow());
	}
	
	@Nonnull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
}
