package fr.raksrinana.rsndiscord.runners.anilist;

import fr.raksrinana.rsndiscord.runners.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.TodoConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.anilist.AniListUtils;
import fr.raksrinana.rsndiscord.utils.anilist.list.MediaList;
import fr.raksrinana.rsndiscord.utils.anilist.queries.MediaListPagedQuery;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.tuple.ImmutablePair;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AniListMediaListScheduledRunner implements AniListRunner<MediaList, MediaListPagedQuery>, ScheduledRunner{
	@Getter
	private final JDA jda;
	@Getter
	private final boolean keepOnlyNew;
	@Getter
	private final boolean sortedByUser;
	
	public AniListMediaListScheduledRunner(@NonNull final JDA jda){
		this(jda, true);
	}
	
	private AniListMediaListScheduledRunner(@NonNull final JDA jda, final boolean keepOnlyNew){
		Log.getLogger(null).info("Creating AniList {} runner", this.getRunnerName());
		this.jda = jda;
		this.keepOnlyNew = keepOnlyNew;
		this.sortedByUser = false;
	}
	
	@Override
	public void run(){
		this.runQueryOnDefaultUsersChannels();
	}
	
	@Override
	public Set<TextChannel> getChannels(){
		return this.getJda().getGuilds().stream().map(g -> Settings.get(g).getAniListConfiguration().getMediaChangeChannel().map(ChannelConfiguration::getChannel).filter(Optional::isPresent).map(Optional::get).orElse(null)).filter(Objects::nonNull).collect(Collectors.toSet());
	}
	
	@NonNull
	@Override
	public String getRunnerName(){
		return "media list";
	}
	
	@Override
	public void sendMessages(@NonNull final Set<TextChannel> channels, @NonNull final Map<User, Set<MediaList>> userElements){
		AniListRunner.super.sendMessages(channels, userElements);
		this.getJda().getGuilds().stream().map(g -> Settings.get(g).getAniListConfiguration().getThaChannel().flatMap(ChannelConfiguration::getChannel)).filter(Optional::isPresent).map(Optional::get).forEach(textChannel -> Settings.get(textChannel.getGuild()).getAniListConfiguration().getThaUser().flatMap(UserConfiguration::getUser).ifPresent(user -> userElements.entrySet().stream().flatMap(e -> e.getValue().stream().map(v -> ImmutablePair.of(e.getKey(), v))).filter(v -> v.getRight().getCustomLists().entrySet().stream().filter(Map.Entry::getValue).anyMatch(entry -> Objects.equals("ThaPending", entry.getKey()) || Objects.equals("ThaReading", entry.getKey()) || Objects.equals("Test", entry.getKey()) || Objects.equals("ThaWatching", entry.getKey()))).forEach(p -> {
			final var similarTodos = Settings.get(textChannel.getGuild()).getTodos().stream().filter(todo -> {
				if(Objects.equals(todo.getMessage().getChannel().getChannelId(), textChannel.getIdLong())){
					return todo.getMessage().getMessage().map(message -> {
						final var isSameMedia = message.getEmbeds().stream().anyMatch(embed -> Objects.equals(embed.getDescription(), p.getRight().getMedia().getTitle().getUserPreferred()));
						return isSameMedia && todo.isDeleteOnDone();
					}).orElse(false);
				}
				return false;
			}).collect(Collectors.toList());
			Actions.sendMessage(textChannel, user.getAsMention(), this.buildMessage(p.getLeft(), p.getRight())).thenAccept(sentMessage -> {
				Actions.addReaction(sentMessage, BasicEmotes.CHECK_OK.getValue());
				similarTodos.forEach(todo -> Utilities.getMessageById(textChannel, todo.getMessage().getMessageId()).thenAccept(messageOptional -> messageOptional.ifPresent(Actions::deleteMessage)));
				Settings.get(textChannel.getGuild()).getTodos().removeAll(similarTodos);
				Settings.get(textChannel.getGuild()).addTodoMessage(new TodoConfiguration(sentMessage, true));
			});
		})));
	}
	
	@NonNull
	@Override
	public MediaListPagedQuery initQuery(@NonNull final Member member){
		return new MediaListPagedQuery(AniListUtils.getUserId(member).orElseThrow());
	}
	
	@NonNull
	@Override
	public String getFetcherID(){
		return "medialist";
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
	
	@Override
	public long getPeriod(){
		return 1;
	}
}
