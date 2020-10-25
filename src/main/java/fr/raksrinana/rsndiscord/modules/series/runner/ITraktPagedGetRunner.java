package fr.raksrinana.rsndiscord.modules.series.runner;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.series.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.modules.series.trakt.model.ITraktDatedObject;
import fr.raksrinana.rsndiscord.modules.series.trakt.model.ITraktObject;
import fr.raksrinana.rsndiscord.modules.series.trakt.requests.ITraktPagedGetRequest;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.UserDateConfiguration;
import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.awt.Color;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public interface ITraktPagedGetRunner<T extends ITraktObject, U extends ITraktPagedGetRequest<T>> extends IScheduledRunner{
	default void runQueryOnDefaultUsersChannels(){
		final var channels = this.getChannels();
		final var members = this.getMembers();
		this.runQuery(members, channels);
	}
	
	Set<TextChannel> getChannels();
	
	String getFetcherID();
	
	default void runQuery(@NonNull final Set<Member> members, @NonNull final Set<TextChannel> channels){
		final var userElements = new HashMap<User, Set<T>>();
		for(final var member : members){
			userElements.computeIfAbsent(member.getUser(), user -> {
				try{
					return this.getElements(member);
				}
				catch(final Exception e){
					Log.getLogger(member.getGuild()).error("Error fetching user {} on Trakt", member, e);
				}
				return null;
			});
		}
		Log.getLogger(null).debug("Trakt API done");
		this.sendMessages(channels, userElements);
	}
	
	@NonNull
	default Set<T> getElements(@NonNull final Member member) throws Exception{
		Log.getLogger(member.getGuild()).debug("Fetching user {}", member);
		var elementList = TraktUtils.getPagedQuery(Settings.getGeneral()
						.getTrakt()
						.getAccessToken(member.getIdLong())
						.orElse(null),
				this.initQuery(member));
		if(!this.isKeepOnlyNew()){
			return elementList;
		}
		final var baseDate = Settings.getGeneral()
				.getTrakt()
				.getLastAccess(this.getFetcherID(), member.getUser().getIdLong())
				.map(UserDateConfiguration::getDate);
		elementList = elementList.stream()
				.filter(e -> e instanceof ITraktDatedObject)
				.filter(e -> baseDate.map(date -> ((ITraktDatedObject) e).getDate().isAfter(date)).orElse(true))
				.collect(Collectors.toSet());
		elementList.stream()
				.filter(e -> e instanceof ITraktDatedObject)
				.map(e -> (ITraktDatedObject) e)
				.map(ITraktDatedObject::getDate)
				.max(ZonedDateTime::compareTo)
				.ifPresent(val -> {
					Log.getLogger(member.getGuild()).debug("New last fetched date for {} on section {}: {} (last was {})", member, this.getFetcherID(), val, baseDate);
					Settings.getGeneral().getTrakt().setLastAccess(member.getUser(), this.getFetcherID(), val);
				});
		return elementList;
	}
	
	default void sendMessages(@NonNull final Set<TextChannel> channels, @NonNull final Map<User, Set<T>> userElements){
		if(this.isSortedByUser()){
			for(final var entry : userElements.entrySet()){
				final var user = entry.getKey();
				entry.getValue().stream()
						.sorted()
						.forEach(change -> channels.stream()
								.filter(channel -> this.sendToChannel(channel, user))
								.forEach(channel -> {
									final var builder = new EmbedBuilder();
									this.buildMessage(channel.getGuild(), builder, user, change);
									Actions.sendEmbed(channel, builder.build());
								}));
			}
		}
		else{
			userElements.entrySet().stream()
					.flatMap(es -> es.getValue().stream().map(val -> Map.entry(es.getKey(), val)))
					.sorted(Map.Entry.comparingByValue())
					.forEach(change -> channels.stream()
							.filter(channel -> this.sendToChannel(channel, change.getKey()))
							.forEach(channel -> {
								final var builder = new EmbedBuilder();
								this.buildMessage(channel.getGuild(), builder, change.getKey(), change.getValue());
								Actions.sendEmbed(channel, builder.build());
							}));
		}
	}
	
	@NonNull U initQuery(@NonNull Member member);
	
	boolean isKeepOnlyNew();
	
	default boolean sendToChannel(final TextChannel channel, final User user){
		return Settings.getGeneral().getTrakt().isRegisteredOn(channel.getGuild(), user);
	}
	
	default boolean isSortedByUser(){
		return false;
	}
	
	default void buildMessage(@NonNull Guild guild, final EmbedBuilder builder, final User user, @NonNull final T change){
		try{
			if(Objects.isNull(user)){
				builder.setAuthor(this.getJda().getSelfUser().getName(), null, this.getJda().getSelfUser().getAvatarUrl());
			}
			else{
				builder.setAuthor(user.getName(), null, user.getAvatarUrl());
			}
			change.fillEmbed(guild, builder);
		}
		catch(final Exception e){
			Log.getLogger(null).error("Error building message for {}", this.getName(), e);
			builder.addField("Error", e.getClass().getName() + " => " + e.getMessage(), false);
			builder.setColor(Color.RED);
		}
	}
	
	default Set<Member> getMembers(){
		return this.getChannels().stream().flatMap(channel -> Settings.getGeneral()
				.getTrakt()
				.getRegisteredUsers()
				.stream()
				.map(user -> channel.getGuild().retrieveMember(user).complete()))
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}
	
	@NonNull JDA getJda();
}
