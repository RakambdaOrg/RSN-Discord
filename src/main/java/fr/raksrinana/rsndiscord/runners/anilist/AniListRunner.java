package fr.raksrinana.rsndiscord.runners.anilist;

import fr.raksrinana.rsndiscord.runners.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.anilist.AniListObject;
import fr.raksrinana.rsndiscord.utils.anilist.AnilistDatedObject;
import fr.raksrinana.rsndiscord.utils.anilist.queries.PagedQuery;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.awt.Color;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public interface AniListRunner<T extends AniListObject, U extends PagedQuery<T>> extends ScheduledRunner{
	default void runQueryOnDefaultUsersChannels(){
		final var channels = this.getChannels();
		final var members = this.getMembers();
		this.runQuery(members, channels);
	}
	
	Set<TextChannel> getChannels();
	
	default void sendMessages(@NonNull final Set<TextChannel> channels, @NonNull final Map<User, Set<T>> userElements){
		userElements.entrySet().stream()
				.flatMap(es -> es.getValue().stream().map(val -> Map.entry(es.getKey(), val)))
				.sorted(Map.Entry.comparingByValue())
				.forEach(change -> channels.stream()
						.filter(channel -> this.sendToChannel(channel, change.getKey()))
						.forEach(channel -> {
							var locale = Settings.get(channel.getGuild()).getLocale();
							var embed = this.buildMessage(locale, change.getKey(), change.getValue());
							Actions.sendMessage(channel, "", embed);
						}));
	}
	
	default void runQuery(@NonNull final Set<Member> members, @NonNull final Set<TextChannel> channels){
		final var userElements = new HashMap<User, Set<T>>();
		for(final var member : members){
			userElements.computeIfAbsent(member.getUser(), user -> {
				try{
					return this.getElements(member);
				}
				catch(final Exception e){
					Log.getLogger(member.getGuild()).error("Error fetching user {} on AniList", member, e);
				}
				return null;
			});
		}
		Log.getLogger(null).debug("AniList API done");
		this.sendMessages(channels, userElements);
	}
	
	@NonNull String getFetcherID();
	
	@NonNull
	default Set<T> getElements(@NonNull final Member member) throws Exception{
		Log.getLogger(member.getGuild()).debug("Fetching user {}", member);
		var elementList = this.initQuery(member).getResult(member);
		if(this.isKeepOnlyNew()){
			final var baseDate = Settings.get(member.getGuild()).getAniListConfiguration().getLastAccess(this.getFetcherID(), member.getUser().getIdLong()).map(UserDateConfiguration::getDate).orElse(ZonedDateTime.now());
			elementList = elementList.stream().filter(e -> e instanceof AnilistDatedObject).filter(e -> ((AnilistDatedObject) e).getDate().isAfter(baseDate)).collect(Collectors.toSet());
			elementList.stream().filter(e -> e instanceof AnilistDatedObject).map(e -> (AnilistDatedObject) e).map(AnilistDatedObject::getDate).max(ZonedDateTime::compareTo).ifPresent(val -> {
				Log.getLogger(member.getGuild()).debug("New last fetched date for {} on section {}: {} (last was {})", member, this.getFetcherID(), val, baseDate);
				Settings.get(member.getGuild()).getAniListConfiguration().setLastAccess(member.getUser(), this.getFetcherID(), val);
			});
		}
		return elementList;
	}
	
	default Set<Member> getMembers(){
		return this.getJda().getGuilds().stream().flatMap(guild -> Settings.get(guild).getAniListConfiguration().getRegisteredMembers(guild).stream()).collect(Collectors.toSet());
	}
	
	@NonNull U initQuery(@NonNull Member member);
	
	boolean isKeepOnlyNew();
	
	@NonNull JDA getJda();
	
	@NonNull
	default MessageEmbed buildMessage(@NonNull Locale locale, final User user, @NonNull final T change){
		final var builder = new EmbedBuilder();
		try{
			if(Objects.isNull(user)){
				builder.setAuthor(this.getJda().getSelfUser().getName(), change.getUrl().toString(), this.getJda().getSelfUser().getAvatarUrl());
			}
			else{
				builder.setAuthor(user.getName(), change.getUrl().toString(), user.getAvatarUrl());
			}
			change.fillEmbed(locale, builder);
		}
		catch(final Exception e){
			Log.getLogger(null).error("Error building message for {}", this.getName(), e);
			builder.addField("Error", e.getClass().getName() + " => " + e.getMessage(), false);
			builder.setColor(Color.RED);
		}
		return builder.build();
	}
	
	default boolean sendToChannel(final TextChannel channel, final User user){
		return Settings.get(channel.getGuild()).getAniListConfiguration().getAccessToken(user.getIdLong()).isPresent();
	}
}
