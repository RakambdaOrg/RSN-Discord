package fr.raksrinana.rsndiscord.runner.trakt;

import fr.raksrinana.rsndiscord.api.trakt.TraktApi;
import fr.raksrinana.rsndiscord.api.trakt.model.ITraktDatedObject;
import fr.raksrinana.rsndiscord.api.trakt.model.ITraktObject;
import fr.raksrinana.rsndiscord.api.trakt.requests.ITraktPagedGetRequest;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.time.ZonedDateTime;
import java.util.*;
import static java.awt.Color.RED;
import static java.util.stream.Collectors.toSet;

public interface ITraktPagedGetRunner<T extends ITraktObject, U extends ITraktPagedGetRequest<T>> extends IScheduledRunner{
	default void runQueryOnDefaultUsersChannels(){
		this.runQuery(getMembers(), getChannels());
	}
	
	Set<TextChannel> getChannels();
	
	String getFetcherID();
	
	default void runQuery(@NonNull final Set<Member> members, @NonNull final Set<TextChannel> channels){
		var userElements = new HashMap<User, Set<T>>();
		for(var member : members){
			userElements.computeIfAbsent(member.getUser(), user -> {
				try{
					return getElements(member);
				}
				catch(final Exception e){
					Log.getLogger(member.getGuild()).error("Error fetching user {} on Trakt", member, e);
				}
				return null;
			});
		}
		Log.getLogger(null).debug("Trakt API done");
		sendMessages(channels, userElements);
	}
	
	default Set<Member> getMembers(){
		return this.getChannels().stream().flatMap(channel -> Settings.getGeneral()
				.getTrakt()
				.getRegisteredUsers()
				.stream()
				.map(user -> channel.getGuild().retrieveMember(user).complete()))
				.filter(Objects::nonNull)
				.collect(toSet());
	}
	
	@NonNull
	default Set<T> getElements(@NonNull final Member member) throws Exception{
		var guild = member.getGuild();
		var user = member.getUser();
		
		Log.getLogger(guild).debug("Fetching user {}", member);
		var elementList = TraktApi.getPagedQuery(Settings.getGeneral()
						.getTrakt()
						.getAccessToken(member.getIdLong())
						.orElse(null),
				initQuery(member));
		
		if(!isKeepOnlyNew()){
			return elementList;
		}
		
		var baseDate = Settings.getGeneral()
				.getTrakt()
				.getLastAccess(getFetcherID(), user.getIdLong())
				.map(UserDateConfiguration::getDate);
		elementList = elementList.stream()
				.filter(e -> e instanceof ITraktDatedObject)
				.filter(e -> baseDate.map(date -> ((ITraktDatedObject) e).getDate().isAfter(date)).orElse(true))
				.collect(toSet());
		elementList.stream()
				.filter(e -> e instanceof ITraktDatedObject)
				.map(e -> (ITraktDatedObject) e)
				.map(ITraktDatedObject::getDate)
				.max(ZonedDateTime::compareTo)
				.ifPresent(val -> {
					Log.getLogger(guild).debug("New last fetched date for {} on section {}: {} (last was {})", member, getFetcherID(), val, baseDate);
					Settings.getGeneral().getTrakt().setLastAccess(user, getFetcherID(), val);
				});
		return elementList;
	}
	
	@NonNull U initQuery(@NonNull Member member);
	
	boolean isKeepOnlyNew();
	
	default boolean sendToChannel(final TextChannel channel, final User user){
		return Settings.getGeneral().getTrakt().isRegisteredOn(channel.getGuild(), user);
	}
	
	default boolean isSortedByUser(){
		return false;
	}
	
	default void sendMessages(@NonNull final Set<TextChannel> channels, @NonNull final Map<User, Set<T>> userElements){
		if(isSortedByUser()){
			for(var entry : userElements.entrySet()){
				var user = entry.getKey();
				entry.getValue().stream()
						.sorted()
						.forEach(change -> channels.stream()
								.filter(channel -> sendToChannel(channel, user))
								.forEach(channel -> {
									var builder = new EmbedBuilder();
									buildMessage(channel.getGuild(), builder, user, change);
									channel.sendMessage(builder.build()).submit();
								}));
			}
		}
		else{
			userElements.entrySet().stream()
					.flatMap(es -> es.getValue().stream().map(val -> Map.entry(es.getKey(), val)))
					.sorted(Map.Entry.comparingByValue())
					.forEach(change -> channels.stream()
							.filter(channel -> sendToChannel(channel, change.getKey()))
							.forEach(channel -> {
								var builder = new EmbedBuilder();
								buildMessage(channel.getGuild(), builder, change.getKey(), change.getValue());
								channel.sendMessage(builder.build()).submit();
							}));
		}
	}
	
	default void buildMessage(@NonNull Guild guild, final EmbedBuilder builder, final User user, @NonNull final T change){
		try{
			var author = Optional.ofNullable(user).orElse(getJda().getSelfUser());
			builder.setAuthor(author.getName(), null, author.getAvatarUrl());
			change.fillEmbed(guild, builder);
		}
		catch(final Exception e){
			Log.getLogger(null).error("Error building message for {}", this.getName(), e);
			builder.addField("Error", e.getClass().getName() + " => " + e.getMessage(), false);
			builder.setColor(RED);
		}
	}
	
	@NonNull JDA getJda();
}
