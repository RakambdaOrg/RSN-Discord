package fr.raksrinana.rsndiscord.runner.impl.trakt;

import fr.raksrinana.rsndiscord.api.trakt.TraktApi;
import fr.raksrinana.rsndiscord.api.trakt.model.ITraktDatedObject;
import fr.raksrinana.rsndiscord.api.trakt.model.ITraktObject;
import fr.raksrinana.rsndiscord.api.trakt.requests.ITraktPagedGetRequest;
import fr.raksrinana.rsndiscord.runner.api.IScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static java.awt.Color.RED;
import static java.util.stream.Collectors.toSet;

public abstract class ITraktPagedGetRunner<T extends ITraktObject, U extends ITraktPagedGetRequest<T>> implements IScheduledRunner{
	@Override
	public void executeGlobal(@NotNull JDA jda){
		runQueryOnDefaultUsersChannels(jda);
	}
	
	@Override
	public void executeGuild(@NotNull Guild guild){
	}
	
	public void runQueryOnDefaultUsersChannels(@NotNull JDA jda){
		runQuery(getMembers(jda), getChannels(jda));
	}
	
	public void runQuery(@NotNull Set<Member> members, @NotNull Set<TextChannel> channels){
		var userElements = new HashMap<User, Set<T>>();
		for(var member : members){
			userElements.computeIfAbsent(member.getUser(), user -> {
				try{
					return getElements(member);
				}
				catch(Exception e){
					LogManager.getLogger(ITraktPagedGetRunner.class).error("Error fetching user {} on Trakt", member, e);
				}
				return null;
			});
		}
		LogManager.getLogger(ITraktPagedGetRunner.class).debug("Trakt API done");
		sendMessages(channels, userElements);
	}
	
	@NotNull
	protected Set<Member> getMembers(@NotNull JDA jda){
		return getChannels(jda).stream().flatMap(channel -> Settings.getGeneral()
						.getTrakt()
						.getRegisteredUsers()
						.stream()
						.map(user -> channel.getGuild().retrieveMember(user).complete()))
				.filter(Objects::nonNull)
				.collect(toSet());
	}
	
	@NotNull
	protected abstract Set<TextChannel> getChannels(@NotNull JDA jda);
	
	@NotNull
	protected Set<T> getElements(@NotNull Member member) throws Exception{
		var user = member.getUser();
		
		LogManager.getLogger(ITraktPagedGetRunner.class).debug("Fetching user {}", member);
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
				.filter(ITraktDatedObject.class::isInstance)
				.filter(e -> baseDate.map(date -> ((ITraktDatedObject) e).getDate().isAfter(date)).orElse(true))
				.collect(toSet());
		elementList.stream()
				.filter(ITraktDatedObject.class::isInstance)
				.map(ITraktDatedObject.class::cast)
				.map(ITraktDatedObject::getDate)
				.max(ZonedDateTime::compareTo)
				.ifPresent(val -> {
					LogManager.getLogger(ITraktPagedGetRunner.class).debug("New last fetched date for {} on section {}: {} (last was {})", member, getFetcherID(), val, baseDate);
					Settings.getGeneral().getTrakt().setLastAccess(user, getFetcherID(), val);
				});
		return elementList;
	}
	
	protected void sendMessages(@NotNull Set<TextChannel> channels, @NotNull Map<User, Set<T>> userElements){
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
									JDAWrappers.message(channel, builder.build()).submit();
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
								JDAWrappers.message(channel, builder.build()).submit();
							}));
		}
	}
	
	@NotNull
	protected abstract U initQuery(@NotNull Member member);
	
	protected abstract boolean isKeepOnlyNew();
	
	@NotNull
	protected abstract String getFetcherID();
	
	protected boolean isSortedByUser(){
		return false;
	}
	
	protected boolean sendToChannel(@NotNull TextChannel channel, @NotNull User user){
		return Settings.getGeneral().getTrakt().isRegisteredOn(channel.getGuild(), user);
	}
	
	protected void buildMessage(@NotNull Guild guild, @NotNull EmbedBuilder builder, @NotNull User user, @NotNull T change){
		try{
			builder.setAuthor(user.getName(), null, user.getAvatarUrl());
			change.fillEmbed(guild, builder);
		}
		catch(Exception e){
			LogManager.getLogger(ITraktPagedGetRunner.class).error("Error building message for {}", getName(), e);
			builder.addField("Error", e.getClass().getName() + " => " + e.getMessage(), false);
			builder.setColor(RED);
		}
	}
}
