package fr.raksrinana.rsndiscord.runners.anilist;

import fr.raksrinana.rsndiscord.runners.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.AniListConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.TodoConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.anilist.AniListUtils;
import fr.raksrinana.rsndiscord.utils.anilist.list.MediaList;
import fr.raksrinana.rsndiscord.utils.anilist.media.Media;
import fr.raksrinana.rsndiscord.utils.anilist.queries.MediaListPagedQuery;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AniListMediaListScheduledRunner implements AniListRunner<MediaList, MediaListPagedQuery>, ScheduledRunner{
	private static final Collection<String> acceptedThaLists = Set.of("Tha");
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
		final var thaChannels = this.getJda().getGuilds().stream().map(Settings::get).map(GuildConfiguration::getAniListConfiguration).map(AniListConfiguration::getThaChannel).flatMap(Optional::stream).map(ChannelConfiguration::getChannel).flatMap(Optional::stream).collect(Collectors.toSet());
		final var thaMembers = this.getJda().getGuilds().stream().flatMap(guild -> Settings.get(guild).getAniListConfiguration().getThaUser().stream().map(UserConfiguration::getUser).flatMap(Optional::stream).map(user -> Optional.ofNullable(guild.getMember(user))).flatMap(Optional::stream)).collect(Collectors.toSet());
		final var mediaListsToSend = userElements.values().stream().flatMap(Set::stream).filter(mediaList -> mediaList.getCustomLists().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).anyMatch(acceptedThaLists::contains)).collect(Collectors.toList());
		thaChannels.forEach(channelToSend -> thaMembers.stream().filter(member -> Objects.equals(channelToSend.getGuild(), member.getGuild())).forEach(memberToSend -> mediaListsToSend.forEach(mediaListToSend -> {
			final var similarTodos = getSimilarTodos(channelToSend, mediaListToSend.getMedia());
			Actions.sendMessage(channelToSend, memberToSend.getAsMention(), this.buildMessage(memberToSend.getUser(), mediaListToSend)).thenAccept(sentMessage -> {
				Actions.addReaction(sentMessage, BasicEmotes.CHECK_OK.getValue());
				similarTodos.forEach(todo -> Utilities.getMessageById(channelToSend, todo.getMessage().getMessageId()).thenAccept(message -> {
					Actions.deleteMessage(message);
					Settings.get(channelToSend.getGuild()).getTodos().remove(todo);
				}));
				Settings.get(channelToSend.getGuild()).addTodoMessage(new TodoConfiguration(sentMessage, true));
			});
		})));
	}
	
	private Collection<TodoConfiguration> getSimilarTodos(@NonNull final TextChannel channel, @NonNull final Media media){
		final var mediaIdStr = Integer.toString(media.getId());
		return Settings.get(channel.getGuild()).getTodos().stream().filter(todo -> {
			if(Objects.equals(todo.getMessage().getChannel().getChannelId(), channel.getIdLong())){
				return todo.getMessage().getMessage().map(message -> {
					final var isSameMedia = message.getEmbeds().stream().anyMatch(embed -> Objects.equals(embed.getTitle(), "User list information") && (Objects.equals(Optional.ofNullable(embed.getFooter()).map(MessageEmbed.Footer::getText).orElse(null), mediaIdStr) || Objects.equals(embed.getDescription(), media.getTitle().getRomaji())));
					return isSameMedia && todo.isDeleteOnDone();
				}).orElse(false);
			}
			return false;
		}).collect(Collectors.toList());
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
