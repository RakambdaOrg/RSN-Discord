package fr.mrcraftcod.gunterdiscord.runners.anilist;

import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.UserDateConfiguration;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListDatedObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.queries.AniListPagedQuery;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public interface AniListRunner<T extends AniListObject, U extends AniListPagedQuery<T>>{
	default void runQueryOnEveryUserAndDefaultChannels(){
		final var channels = getJDA().getGuilds().stream().map(g -> NewSettings.getConfiguration(g).getAniListConfiguration().getNotificationsChannel().map(ChannelConfiguration::getChannel).filter(Optional::isPresent).map(Optional::get).orElse(null)).filter(Objects::nonNull).collect(Collectors.toList());
		final var members = channels.stream().flatMap(channel -> NewSettings.getConfiguration(channel.getGuild()).getAniListConfiguration().getRegisteredUsers().stream().map(user -> channel.getGuild().getMember(user))).collect(Collectors.toList());
		runQuery(members, channels);
	}
	
	default void runQuery(@Nonnull final List<Member> members, @Nonnull final List<TextChannel> channels){
		getLogger(null).info("Starting AniList {} runner", getRunnerName());
		try{
			final var userElements = new HashMap<User, List<T>>();
			for(final var member : members){
				userElements.computeIfAbsent(member.getUser(), user -> {
					try{
						return getElements(member);
					}
					catch(final Exception e){
						getLogger(member.getGuild()).error("Error fetching user {} on AniList", member, e);
					}
					return null;
				});
			}
			getLogger(null).debug("AniList API done");
			sendMessages(channels, userElements);
			getLogger(null).info("AniList {} runner done", getRunnerName());
		}
		catch(final Exception e){
			getLogger(null).error("Error in AniList {} runner", getRunnerName(), e);
		}
	}
	
	@Nonnull
	String getRunnerName();
	
	@Nonnull
	default List<T> getElements(@Nonnull final Member member) throws Exception{
		getLogger(member.getGuild()).debug("Fetching user {}", member);
		final var lastAccess = NewSettings.getConfiguration(member.getGuild()).getAniListConfiguration().getLastAccess();
		var elementList = initQuery(member).getResult(member);
		if(keepOnlyNew()){
			final var baseDate = NewSettings.getConfiguration(member.getGuild()).getAniListConfiguration().getLastAccess("lastFetch", member.getUser().getIdLong()).map(UserDateConfiguration::getDate).orElse(LocalDateTime.MIN);
			elementList = elementList.stream().filter(e -> e instanceof AniListDatedObject).filter(e -> ((AniListDatedObject) e).getDate().isAfter(baseDate)).collect(Collectors.toList());
		}
		elementList.stream().filter(e -> e instanceof AniListDatedObject).map(e -> (AniListDatedObject) e).map(AniListDatedObject::getDate).max(LocalDateTime::compareTo).ifPresent(val -> {
			getLogger(member.getGuild()).debug("New last fetched date for {}: {}", member, val);
			NewSettings.getConfiguration(member.getGuild()).getAniListConfiguration().setLastAccess(member.getUser(), "lastFetch", val);
		});
		return elementList;
	}
	
	default boolean sortedByUser(){
		return false;
	}
	
	default void sendMessages(@Nonnull final List<TextChannel> channels, @Nonnull final Map<User, List<T>> userElements){
		if(sortedByUser()){
			for(final var user : userElements.keySet()){
				final var element = userElements.get(user);
				element.stream().sorted().map(change -> buildMessage(user, change)).forEach(message -> channels.stream().filter(channel -> sendToChannel(channel, user)).forEach(channel -> Actions.sendMessage(channel, message)));
			}
		}
		else{
			userElements.entrySet().stream().flatMap(es -> es.getValue().stream().map(val -> Map.entry(es.getKey(), val))).sorted(Comparator.comparing(Map.Entry::getValue)).map(change -> Map.entry(change.getKey(), buildMessage(change.getKey(), change.getValue()))).forEach(infos -> channels.stream().filter(chan -> sendToChannel(chan, infos.getKey())).forEach(chan -> Actions.sendMessage(chan, infos.getValue())));
		}
	}
	
	@Nonnull
	U initQuery(@Nonnull Member member);
	
	@Nonnull
	String getFetcherID();
	
	@Nonnull
	default MessageEmbed buildMessage(@Nullable final User user, @Nonnull final T change){
		final var builder = new EmbedBuilder();
		if(Objects.isNull(user)){
			builder.setAuthor(getJDA().getSelfUser().getName(), change.getUrl().toString(), getJDA().getSelfUser().getAvatarUrl());
		}
		else{
			builder.setAuthor(user.getName(), change.getUrl().toString(), user.getAvatarUrl());
		}
		change.fillEmbed(builder);
		return builder.build();
	}
	
	default boolean sendToChannel(final TextChannel channel, final User user){
		return NewSettings.getConfiguration(channel.getGuild()).getAniListConfiguration().getAccessToken(user.getIdLong()).isPresent();
	}
	
	boolean keepOnlyNew();
	
	@Nonnull
	JDA getJDA();
}
