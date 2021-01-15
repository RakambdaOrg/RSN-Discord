package fr.raksrinana.rsndiscord.runner.anilist;

import fr.raksrinana.rsndiscord.api.anilist.data.IAniListDatedObject;
import fr.raksrinana.rsndiscord.api.anilist.data.IAniListObject;
import fr.raksrinana.rsndiscord.api.anilist.query.IPagedQuery;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static java.awt.Color.RED;
import static java.util.stream.Collectors.toSet;

public interface IAniListRunner<T extends IAniListObject, U extends IPagedQuery<T>> extends IScheduledRunner{
	default void runQueryOnDefaultUsersChannels(){
		runQuery(getMembers(), getChannels());
	}
	
	Set<TextChannel> getChannels();
	
	default void runQuery(@NonNull final Set<Member> members, @NonNull final Set<TextChannel> channels){
		var userElements = new HashMap<User, Set<T>>();
		for(var member : members){
			userElements.computeIfAbsent(member.getUser(), user -> {
				try{
					return getElements(member);
				}
				catch(final Exception e){
					Log.getLogger(member.getGuild()).error("Error fetching user {} on AniList", member, e);
				}
				return null;
			});
		}
		Log.getLogger(null).debug("AniList API done");
		sendMessages(channels, userElements);
	}
	
	default Set<Member> getMembers(){
		return getJda().getGuilds().stream()
				.flatMap(guild -> Settings.getGeneral().getAniList().getRegisteredMembers(guild).stream())
				.collect(toSet());
	}
	
	@NonNull String getFetcherID();
	
	@NonNull
	default Set<T> getElements(@NonNull final Member member) throws Exception{
		var guild = member.getGuild();
		var user = member.getUser();
		Log.getLogger(guild).debug("Fetching user {}", member);
		
		var elementList = initQuery(member).getResult(member);
		if(!isKeepOnlyNew()){
			return elementList;
		}
		
		var aniListGeneral = Settings.getGeneral().getAniList();
		var baseDate = aniListGeneral.getLastAccess(getFetcherID(), user.getIdLong())
				.map(UserDateConfiguration::getDate);
		
		elementList = elementList.stream()
				.filter(e -> e instanceof IAniListDatedObject)
				.filter(e -> baseDate.map(date -> ((IAniListDatedObject) e).getDate().isAfter(date)).orElse(true))
				.collect(toSet());
		elementList.stream().filter(e -> e instanceof IAniListDatedObject)
				.map(e -> (IAniListDatedObject) e)
				.map(IAniListDatedObject::getDate)
				.max(ZonedDateTime::compareTo)
				.ifPresent(val -> {
					Log.getLogger(guild).debug("New last fetched date for {} on section {}: {} (last was {})", member, getFetcherID(), val, baseDate);
					aniListGeneral.setLastAccess(user, getFetcherID(), val);
				});
		return elementList;
	}
	
	default void sendMessages(@NonNull final Set<TextChannel> channels, @NonNull final Map<User, Set<T>> userElements){
		userElements.entrySet().stream()
				.flatMap(entry -> entry.getValue().stream().map(val -> Map.entry(entry.getKey(), val)))
				.sorted(Map.Entry.comparingByValue())
				.forEach(change -> channels.stream()
						.filter(channel -> shouldSendTo(channel, change.getKey()))
						.forEach(channel -> {
							var embed = buildMessage(channel.getGuild(), change.getKey(), change.getValue());
							channel.sendMessage(embed).submit();
						}));
	}
	
	@NonNull U initQuery(@NonNull Member member);
	
	boolean isKeepOnlyNew();
	
	@NonNull JDA getJda();
	
	default boolean shouldSendTo(final TextChannel channel, final User user){
		return Settings.getGeneral().getAniList().isRegisteredOn(channel.getGuild(), user);
	}
	
	@NonNull
	default MessageEmbed buildMessage(@NonNull Guild guild, final User user, @NonNull final T change){
		var builder = new EmbedBuilder();
		
		try{
			var author = Optional.ofNullable(user)
					.orElse(getJda().getSelfUser());
			builder.setAuthor(author.getName(), change.getUrl().toString(), author.getAvatarUrl());
			change.fillEmbed(guild, builder);
		}
		catch(final Exception e){
			Log.getLogger(null).error("Error building message for {}", this.getName(), e);
			builder.addField("Error", e.getClass().getName() + " => " + e.getMessage(), false)
					.setColor(RED);
		}
		return builder.build();
	}
}
