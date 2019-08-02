package fr.mrcraftcod.gunterdiscord.runners.anilist;

import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.settings.types.UserDateConfiguration;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListDatedObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.queries.AniListPagedQuery;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
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
		final var channels = this.getChannels();
		final var members = channels.stream().flatMap(channel -> NewSettings.getConfiguration(channel.getGuild()).getAniListConfiguration().getRegisteredUsers().stream().map(user -> channel.getGuild().getMember(user))).collect(Collectors.toList());
		this.runQuery(members, channels);
	}
	
	List<TextChannel> getChannels();
	
	default void runQuery(@Nonnull final List<Member> members, @Nonnull final List<TextChannel> channels){
		getLogger(null).info("Starting AniList {} runner", this.getRunnerName());
		try{
			final var userElements = new HashMap<User, List<T>>();
			for(final var member : members){
				userElements.computeIfAbsent(member.getUser(), user -> {
					try{
						return this.getElements(member);
					}
					catch(final Exception e){
						getLogger(member.getGuild()).error("Error fetching user {} on AniList", member, e);
					}
					return null;
				});
			}
			getLogger(null).debug("AniList API done");
			this.sendMessages(channels, userElements);
			getLogger(null).info("AniList {} runner done", this.getRunnerName());
		}
		catch(final Exception e){
			getLogger(null).error("Error in AniList {} runner", this.getRunnerName(), e);
		}
	}
	
	@Nonnull
	String getRunnerName();
	
	@Nonnull
	default List<T> getElements(@Nonnull final Member member) throws Exception{
		getLogger(member.getGuild()).debug("Fetching user {}", member);
		var elementList = this.initQuery(member).getResult(member);
		if(this.keepOnlyNew()){
			final var baseDate = NewSettings.getConfiguration(member.getGuild()).getAniListConfiguration().getLastAccess(this.getRunnerName(), member.getUser().getIdLong()).map(UserDateConfiguration::getDate).orElse(LocalDateTime.of(2019, 7, 7, 0, 0));
			elementList = elementList.stream().filter(e -> e instanceof AniListDatedObject).filter(e -> ((AniListDatedObject) e).getDate().isAfter(baseDate)).collect(Collectors.toList());
		}
		elementList.stream().filter(e -> e instanceof AniListDatedObject).map(e -> (AniListDatedObject) e).map(AniListDatedObject::getDate).max(LocalDateTime::compareTo).ifPresent(val -> {
			getLogger(member.getGuild()).debug("New last fetched date for {} on section {}: {}", member, this.getRunnerName(), val);
			NewSettings.getConfiguration(member.getGuild()).getAniListConfiguration().setLastAccess(member.getUser(), this.getRunnerName(), val);
		});
		return elementList;
	}
	
	default boolean sortedByUser(){
		return false;
	}
	
	default void sendMessages(@Nonnull final List<TextChannel> channels, @Nonnull final Map<User, List<T>> userElements){
		if(this.sortedByUser()){
			for(final var entry : userElements.entrySet()){
				final var user = entry.getKey();
				final var element = entry.getValue();
				element.stream().sorted().map(change -> this.buildMessage(user, change)).forEach(message -> channels.stream().filter(channel -> this.sendToChannel(channel, user)).forEach(channel -> Actions.sendMessage(channel, message)));
			}
		}
		else{
			userElements.entrySet().stream().flatMap(es -> es.getValue().stream().map(val -> Map.entry(es.getKey(), val))).sorted(Comparator.comparing(Map.Entry::getValue)).map(change -> Map.entry(change.getKey(), this.buildMessage(change.getKey(), change.getValue()))).forEach(infos -> channels.stream().filter(chan -> this.sendToChannel(chan, infos.getKey())).forEach(chan -> Actions.sendMessage(chan, infos.getValue())));
		}
	}
	
	@Nonnull
	U initQuery(@Nonnull Member member);
	
	@Nonnull
	String getFetcherID();
	
	@Nonnull
	default MessageEmbed buildMessage(@Nullable final User user, @Nonnull final T change){
		final var builder = new EmbedBuilder();
		try{
			if(Objects.isNull(user)){
				builder.setAuthor(this.getJDA().getSelfUser().getName(), change.getUrl().toString(), this.getJDA().getSelfUser().getAvatarUrl());
			}
			else{
				builder.setAuthor(user.getName(), change.getUrl().toString(), user.getAvatarUrl());
			}
			change.fillEmbed(builder);
		}
		catch(final Exception e){
			Log.getLogger(null).error("Error with AniList {} runner", this.getRunnerName(), e);
			builder.addField("Error", e.getClass().getName() + " => " + e.getMessage(), false);
			builder.setColor(Color.RED);
		}
		return builder.build();
	}
	
	default boolean sendToChannel(final TextChannel channel, final User user){
		return NewSettings.getConfiguration(channel.getGuild()).getAniListConfiguration().getAccessToken(user.getIdLong()).isPresent();
	}
	
	boolean keepOnlyNew();
	
	@Nonnull
	JDA getJDA();
}
