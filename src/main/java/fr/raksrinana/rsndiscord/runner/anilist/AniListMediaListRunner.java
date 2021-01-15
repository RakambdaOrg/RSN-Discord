package fr.raksrinana.rsndiscord.runner.anilist;

import fr.raksrinana.rsndiscord.api.anilist.AniListApi;
import fr.raksrinana.rsndiscord.api.anilist.data.list.MediaList;
import fr.raksrinana.rsndiscord.api.anilist.data.media.IMedia;
import fr.raksrinana.rsndiscord.api.anilist.query.MediaListPagedQuery;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.anilist.AniListConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.reaction.ReactionTag.ANILIST_TODO;
import static fr.raksrinana.rsndiscord.reaction.ReactionUtils.DELETE_KEY;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.CHECK_OK;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.stream.Collectors.toList;

@ScheduledRunner
public class AniListMediaListRunner implements IAniListRunner<MediaList, MediaListPagedQuery>{
	private static final Collection<String> acceptedThaLists = Set.of("Tha");
	@Getter
	private final JDA jda;
	
	public AniListMediaListRunner(@NonNull final JDA jda){
		this.jda = jda;
	}
	
	@Override
	public Set<TextChannel> getChannels(){
		return getJda().getGuilds().stream()
				.flatMap(g -> Settings.get(g).getAniListConfiguration()
						.getMediaChangeChannel()
						.flatMap(ChannelConfiguration::getChannel)
						.stream())
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}
	
	@Override
	public void sendMessages(@NonNull final Set<TextChannel> channels, @NonNull final Map<User, Set<MediaList>> userElements){
		IAniListRunner.super.sendMessages(channels, userElements);
		var thaChannels = getJda().getGuilds().stream()
				.map(Settings::get)
				.map(GuildConfiguration::getAniListConfiguration)
				.map(AniListConfiguration::getThaChannel)
				.flatMap(Optional::stream)
				.map(ChannelConfiguration::getChannel)
				.flatMap(Optional::stream)
				.collect(Collectors.toSet());
		var mediaListsToSend = userElements.values().stream().flatMap(Set::stream)
				.filter(mediaList -> mediaList.getCustomLists()
						.entrySet().stream()
						.filter(Map.Entry::getValue)
						.map(Map.Entry::getKey)
						.anyMatch(acceptedThaLists::contains))
				.collect(toList());
		thaChannels.forEach(channelToSend -> Settings.get(channelToSend.getGuild())
				.getAniListConfiguration()
				.getThaUser()
				.flatMap(UserConfiguration::getUser)
				.map(channelToSend.getGuild()::retrieveMember)
				.map(RestAction::complete)
				.ifPresent(memberToSend -> mediaListsToSend.stream()
						.sorted()
						.forEach(mediaListToSend -> sendMediaList(channelToSend, memberToSend, mediaListToSend))));
	}
	
	private void sendMediaList(TextChannel channel, Member member, MediaList mediaList){
		
		channel.sendMessage(member.getAsMention())
				.embed(buildMessage(channel.getGuild(), member.getUser(), mediaList))
				.submit()
				.thenAccept(message -> {
					message.addReaction(CHECK_OK.getValue()).submit();
					
					deleteSimilarPendingMedia(channel, mediaList);
					
					var reactionMessageConfiguration = new WaitingReactionMessageConfiguration(message, ANILIST_TODO,
							Map.of(DELETE_KEY, Boolean.toString(true)));
					Settings.get(channel.getGuild()).addMessagesAwaitingReaction(reactionMessageConfiguration);
				});
	}
	
	private void deleteSimilarPendingMedia(TextChannel channel, MediaList mediaList){
		getSimilarWaitingReactions(channel, mediaList.getMedia())
				.forEach(reaction -> channel.deleteMessageById(reaction.getMessage().getMessageId()).submit()
						.thenAccept(empty -> Settings.get(channel.getGuild()).removeMessagesAwaitingReaction(reaction))
						.exceptionally(th -> {
							Log.getLogger(channel.getGuild()).error("Failed to delete similar pending media", th);
							Utilities.reportException("Failed to delete similar pending media", th);
							return null;
						}));
	}
	
	private static Collection<WaitingReactionMessageConfiguration> getSimilarWaitingReactions(@NonNull final TextChannel channel, @NonNull final IMedia media){
		var footer = "ID: " + media.getId();
		return Settings.get(channel.getGuild()).getMessagesAwaitingReaction(ANILIST_TODO).stream()
				.filter(reaction -> {
					if(Objects.equals(reaction.getMessage().getChannel().getChannelId(), channel.getIdLong())){
						return reaction.getMessage().getMessage()
								.map(message -> isSameMedia(footer, reaction, message))
								.orElse(false);
					}
					return false;
				}).collect(toList());
	}
	
	@NonNull
	private static Boolean isSameMedia(String footer, WaitingReactionMessageConfiguration reaction, Message message){
		var isDeleteMode = ofNullable(reaction.getData().get(DELETE_KEY)).map(Boolean::parseBoolean).orElse(false);
		var isSameMedia = message.getEmbeds().stream()
				.anyMatch(embed -> Objects.equals(embed.getTitle(), "User list information")
						&& Objects.equals(ofNullable(embed.getFooter()).map(MessageEmbed.Footer::getText).orElse(null), footer));
		return isSameMedia && isDeleteMode;
	}
	
	@NonNull
	@Override
	public MediaListPagedQuery initQuery(@NonNull final Member member){
		return new MediaListPagedQuery(AniListApi.getUserId(member).orElseThrow());
	}
	
	@Override
	public void execute(){
		this.runQueryOnDefaultUsersChannels();
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
	public String getName(){
		return "AniList media list";
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return HOURS;
	}
	
	@Override
	public long getPeriod(){
		return 1;
	}
	
	@Override
	public boolean isKeepOnlyNew(){
		return true;
	}
}
