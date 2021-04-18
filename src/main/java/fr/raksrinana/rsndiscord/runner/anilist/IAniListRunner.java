package fr.raksrinana.rsndiscord.runner.anilist;

import fr.raksrinana.rsndiscord.api.anilist.data.IAniListDatedObject;
import fr.raksrinana.rsndiscord.api.anilist.data.IAniListObject;
import fr.raksrinana.rsndiscord.api.anilist.query.IPagedQuery;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;
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
	
	default void runQuery(@NotNull Set<Member> members, @NotNull Set<TextChannel> channels){
		var userElements = new HashMap<User, Set<T>>();
		for(var member : members){
			userElements.computeIfAbsent(member.getUser(), user -> {
				try{
					return getElements(member);
				}
				catch(Exception e){
					Log.getLogger(member.getGuild()).error("Error fetching user {} on AniList", member, e);
				}
				return null;
			});
		}
		Log.getLogger().debug("AniList API done");
		sendMessages(channels, userElements);
	}
	
	@NotNull
	default Set<T> getElements(@NotNull Member member) throws Exception{
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
	
	default void sendMessages(@NotNull Set<TextChannel> channels, @NotNull Map<User, Set<T>> userElements){
		userElements.entrySet().stream()
				.flatMap(entry -> entry.getValue().stream().map(val -> Map.entry(entry.getKey(), val)))
				.sorted(Map.Entry.comparingByValue())
				.forEach(change -> channels.stream()
						.filter(channel -> shouldSendTo(channel, change.getKey()))
						.forEach(channel -> {
							var embed = buildMessage(channel.getGuild(), change.getKey(), change.getValue());
							JDAWrappers.message(channel, embed).submit();
						}));
	}
	
	@NotNull U initQuery(@NotNull Member member);
	
	@NotNull String getFetcherID();
	
	default boolean shouldSendTo(@NotNull TextChannel channel, @NotNull User user){
		return Settings.getGeneral().getAniList().isRegisteredOn(channel.getGuild(), user);
	}
	
	@NotNull
	default MessageEmbed buildMessage(@NotNull Guild guild, User user, @NotNull T change){
		var builder = new EmbedBuilder();
		
		try{
			var author = Optional.ofNullable(user)
					.orElse(getJda().getSelfUser());
			builder.setAuthor(author.getName(), change.getUrl().toString(), author.getAvatarUrl());
			change.fillEmbed(guild, builder);
		}
		catch(Exception e){
			Log.getLogger().error("Error building message for {}", getName(), e);
			builder.addField("Error", e.getClass().getName() + " => " + e.getMessage(), false)
					.setColor(RED);
		}
		return builder.build();
	}
	
	boolean isKeepOnlyNew();
	
	@NotNull JDA getJda();
	
	@NotNull
	Set<TextChannel> getChannels();
	
	@NotNull
	default Set<Member> getMembers(){
		return getJda().getGuilds().stream()
				.flatMap(guild -> Settings.getGeneral().getAniList().getRegisteredMembers(guild).stream())
				.collect(toSet());
	}
}
