package fr.raksrinana.rsndiscord.runners.anilist;

import fr.raksrinana.rsndiscord.settings.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.AniListConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.anilist.AniListUtils;
import fr.raksrinana.rsndiscord.utils.anilist.list.MediaList;
import fr.raksrinana.rsndiscord.utils.anilist.media.Media;
import fr.raksrinana.rsndiscord.utils.anilist.queries.MediaListPagedQuery;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionUtils;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AniListMediaListScheduledRunner implements AniListRunner<MediaList, MediaListPagedQuery>{
	private static final Collection<String> acceptedThaLists = Set.of("Tha");
	@Getter
	private final JDA jda;
	@Getter
	private final boolean keepOnlyNew;
	
	public AniListMediaListScheduledRunner(@NonNull final JDA jda){
		this(jda, true);
	}
	
	private AniListMediaListScheduledRunner(@NonNull final JDA jda, final boolean keepOnlyNew){
		this.jda = jda;
		this.keepOnlyNew = keepOnlyNew;
	}
	
	@Override
	public void execute(){
		this.runQueryOnDefaultUsersChannels();
	}
	
	@Override
	public Set<TextChannel> getChannels(){
		return this.getJda().getGuilds().stream().map(g -> Settings.get(g).getAniListConfiguration().getMediaChangeChannel().map(ChannelConfiguration::getChannel).filter(Optional::isPresent).map(Optional::get).orElse(null)).filter(Objects::nonNull).collect(Collectors.toSet());
	}
	
	@NonNull
	@Override
	public String getName(){
		return "AniList media list";
	}
	
	@Override
	public void sendMessages(@NonNull final Set<TextChannel> channels, @NonNull final Map<User, Set<MediaList>> userElements){
		AniListRunner.super.sendMessages(channels, userElements);
		final var thaChannels = this.getJda().getGuilds().stream().map(Settings::get).map(GuildConfiguration::getAniListConfiguration).map(AniListConfiguration::getThaChannel).flatMap(Optional::stream).map(ChannelConfiguration::getChannel).flatMap(Optional::stream).collect(Collectors.toSet());
		final var mediaListsToSend = userElements.values().stream().flatMap(Set::stream).filter(mediaList -> mediaList.getCustomLists().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).anyMatch(acceptedThaLists::contains)).collect(Collectors.toList());
		thaChannels.forEach(channelToSend -> Settings.get(channelToSend.getGuild()).getAniListConfiguration().getThaUser().flatMap(UserConfiguration::getUser).map(channelToSend.getGuild()::retrieveMember).map(RestAction::complete).ifPresent(memberToSend -> mediaListsToSend.forEach(mediaListToSend -> {
			final var similarWaitingReactions = getSimilarWaitingReactions(channelToSend, mediaListToSend.getMedia());
			Actions.sendMessage(channelToSend, memberToSend.getAsMention(), this.buildMessage(memberToSend.getUser(), mediaListToSend)).thenAccept(sentMessage -> {
				Actions.addReaction(sentMessage, BasicEmotes.CHECK_OK.getValue());
				similarWaitingReactions.forEach(reaction -> Utilities.getMessageById(channelToSend, reaction.getMessage().getMessageId()).thenAccept(message -> {
					Actions.deleteMessage(message);
					Settings.get(channelToSend.getGuild()).removeMessagesAwaitingReaction(reaction);
				}));
				Settings.get(channelToSend.getGuild()).addMessagesAwaitingReaction(new WaitingReactionMessageConfiguration(sentMessage, ReactionTag.ANILIST_TODO, Map.of(ReactionUtils.DELETE_KEY, Boolean.toString(true))));
			});
		})));
	}
	
	private Collection<WaitingReactionMessageConfiguration> getSimilarWaitingReactions(@NonNull final TextChannel channel, @NonNull final Media media){
		final var mediaIdStr = Integer.toString(media.getId());
		return Settings.get(channel.getGuild()).getMessagesAwaitingReaction(ReactionTag.ANILIST_TODO).stream().filter(reaction -> Objects.equals(reaction.getTag(), ReactionTag.ANILIST_TODO)).filter(reaction -> {
			if(Objects.equals(reaction.getMessage().getChannel().getChannelId(), channel.getIdLong())){
				return reaction.getMessage().getMessage().map(message -> {
					final var isSameMedia = message.getEmbeds().stream().anyMatch(embed -> Objects.equals(embed.getTitle(), "User list information") && (Objects.equals(Optional.ofNullable(embed.getFooter()).map(MessageEmbed.Footer::getText).orElse(null), mediaIdStr) || Objects.equals(embed.getDescription(), media.getTitle().getRomaji())));
					return isSameMedia && Optional.ofNullable(reaction.getData().get(ReactionUtils.DELETE_KEY)).map(Boolean::parseBoolean).orElse(false);
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
