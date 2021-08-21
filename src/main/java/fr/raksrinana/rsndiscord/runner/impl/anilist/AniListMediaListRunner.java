package fr.raksrinana.rsndiscord.runner.impl.anilist;

import fr.raksrinana.rsndiscord.api.anilist.AniListApi;
import fr.raksrinana.rsndiscord.api.anilist.data.list.MediaList;
import fr.raksrinana.rsndiscord.api.anilist.query.MediaListPagedQuery;
import fr.raksrinana.rsndiscord.components.impl.button.AniListMediaCompletedButtonHandler;
import fr.raksrinana.rsndiscord.components.impl.button.AniListMediaDiscardedButtonHandler;
import fr.raksrinana.rsndiscord.runner.api.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.impl.guild.anilist.AniListConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.stream.Collectors.toList;

@ScheduledRunner
public class AniListMediaListRunner extends IAniListRunner<MediaList, MediaListPagedQuery>{
	private static final Collection<String> acceptedThaLists = Set.of("Tha");
	
	private void sendMediaList(TextChannel channel, Member member, MediaList mediaList){
		var completedButton = new AniListMediaCompletedButtonHandler();
		var discardedButton = new AniListMediaDiscardedButtonHandler();
		
		JDAWrappers.message(channel, member.getAsMention())
				.embed(buildMessage(channel.getGuild(), member.getUser(), mediaList))
				.addActionRow(completedButton.asComponent(), discardedButton.asComponent())
				.submit();
	}
	
	@NotNull
	@Override
	public String getName(){
		return "AniList media list";
	}
	
	@NotNull
	@Override
	public TimeUnit getPeriodUnit(){
		return HOURS;
	}
	
	@Override
	@NotNull
	public Set<TextChannel> getChannels(@NotNull JDA jda){
		return jda.getGuilds().stream()
				.flatMap(g -> Settings.get(g).getAniListConfiguration()
						.getMediaChangeChannel()
						.flatMap(ChannelConfiguration::getChannel)
						.stream())
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}
	
	@NotNull
	@Override
	public String getFetcherID(){
		return "medialist";
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@Override
	public void sendMessages(@NotNull JDA jda, @NotNull Set<TextChannel> channels, @NotNull Map<User, Set<MediaList>> userElements){
		super.sendMessages(jda, channels, userElements);
		var thaChannels = jda.getGuilds().stream()
				.map(Settings::get)
				.map(GuildConfiguration::getAniListConfiguration)
				.map(AniListConfiguration::getThaChannel)
				.flatMap(Optional::stream)
				.map(ChannelConfiguration::getChannel)
				.flatMap(Optional::stream)
				.collect(Collectors.toSet());
		var mediaListsToSend = userElements.values().stream().flatMap(Set::stream)
				.filter(mediaList -> Optional.ofNullable(mediaList.getCustomLists())
						.orElse(new HashMap<>())
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
	
	@NotNull
	@Override
	public MediaListPagedQuery initQuery(@NotNull Member member){
		return new MediaListPagedQuery(AniListApi.getUserId(member).orElseThrow());
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
