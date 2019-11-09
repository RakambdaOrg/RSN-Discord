package fr.raksrinana.rsndiscord.runners.anilist;

import fr.raksrinana.rsndiscord.runners.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.TodoConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.anilist.AniListUtils;
import fr.raksrinana.rsndiscord.utils.anilist.list.MediaList;
import fr.raksrinana.rsndiscord.utils.anilist.queries.MediaListPagedQuery;
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
public class AniListMediaListScheduledRunner implements AniListRunner<MediaList, MediaListPagedQuery>, ScheduledRunner{
	private final JDA jda;
	private final boolean keepOnlyNew;
	private final boolean sortedByUser;
	
	public AniListMediaListScheduledRunner(@Nonnull final JDA jda){
		this(jda, true);
	}
	
	private AniListMediaListScheduledRunner(@Nonnull final JDA jda, final boolean keepOnlyNew){
		Log.getLogger(null).info("Creating AniList {} runner", this.getRunnerName());
		this.jda = jda;
		this.keepOnlyNew = keepOnlyNew;
		this.sortedByUser = false;
	}
	
	@Override
	public void run(){
		this.runQueryOnEveryUserAndDefaultChannels();
	}
	
	@Override
	public List<TextChannel> getChannels(){
		return this.getJDA().getGuilds().stream().map(g -> Settings.getConfiguration(g).getAniListConfiguration().getMediaChangeChannel().map(ChannelConfiguration::getChannel).filter(Optional::isPresent).map(Optional::get).orElse(null)).filter(Objects::nonNull).collect(Collectors.toList());
	}
	
	@Nonnull
	@Override
	public String getRunnerName(){
		return "media list";
	}
	
	@Override
	public void sendMessages(@Nonnull final List<TextChannel> channels, @Nonnull final Map<User, List<MediaList>> userElements){
		AniListRunner.super.sendMessages(channels, userElements);
		this.getJDA().getGuilds().stream().map(g -> Settings.getConfiguration(g).getAniListConfiguration().getThaChannel().flatMap(ChannelConfiguration::getChannel)).filter(Optional::isPresent).map(Optional::get).forEach(textChannel -> Settings.getConfiguration(textChannel.getGuild()).getAniListConfiguration().getThaUser().flatMap(UserConfiguration::getUser).ifPresent(user -> userElements.entrySet().stream().flatMap(e -> e.getValue().stream().map(v -> ImmutablePair.of(e.getKey(), v))).filter(v -> v.getRight().getCustomLists().entrySet().stream().filter(Map.Entry::getValue).anyMatch(entry -> Objects.equals("ThaPending", entry.getKey()) || Objects.equals("ThaReading", entry.getKey()) || Objects.equals("ThaWatching", entry.getKey()))).forEach(p -> Actions.sendMessage(textChannel, user.getAsMention(), this.buildMessage(p.getLeft(), p.getRight()), sentMessage -> {
			sentMessage.addReaction(BasicEmotes.CHECK_OK.getValue()).queue();
			Settings.getConfiguration(textChannel.getGuild()).getTodos().removeIf(todo -> {
				if(Objects.equals(todo.getMessage().getChannel().getChannelId(), sentMessage.getChannel().getIdLong())){
					return todo.getMessage().getMessage().map(message -> {
						final var isSameMedia = message.getEmbeds().stream().anyMatch(embed -> Objects.equals(embed.getDescription(), p.getRight().getMedia().getTitle().getUserPreferred()));
						if(isSameMedia && todo.isDeleteOnDone()){
							Actions.deleteMessage(message);
							return true;
						}
						return false;
					}).orElse(false);
				}
				return false;
			});
			Settings.getConfiguration(textChannel.getGuild()).addTodoMessage(new TodoConfiguration(sentMessage, true));
		}))));
	}
	
	@Nonnull
	@Override
	public MediaListPagedQuery initQuery(@Nonnull final Member member){
		return new MediaListPagedQuery(AniListUtils.getUserId(member).orElseThrow());
	}
	
	@Override
	public boolean keepOnlyNew(){
		return this.keepOnlyNew;
	}
	
	@Override
	public boolean sortedByUser(){
		return this.sortedByUser;
	}
	
	@Nonnull
	@Override
	public JDA getJDA(){
		return this.jda;
	}
	
	@Nonnull
	@SuppressWarnings("SpellCheckingInspection")
	@Override
	public String getFetcherID(){
		return "medialist";
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@Nonnull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
	
	@Override
	public long getPeriod(){
		return 1;
	}
}
