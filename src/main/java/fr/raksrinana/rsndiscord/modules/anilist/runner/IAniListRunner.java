package fr.raksrinana.rsndiscord.modules.anilist.runner;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.anilist.data.IAniListObject;
import fr.raksrinana.rsndiscord.modules.anilist.data.IAnilistDatedObject;
import fr.raksrinana.rsndiscord.modules.anilist.query.IPagedQuery;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.types.UserDateConfiguration;
import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import java.awt.Color;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public interface IAniListRunner<T extends IAniListObject, U extends IPagedQuery<T>> extends IScheduledRunner{
	default void runQueryOnDefaultUsersChannels(){
		final var channels = this.getChannels();
		final var members = this.getMembers();
		this.runQuery(members, channels);
	}
	
	Set<TextChannel> getChannels();
	
	default void sendMessages(@NonNull final Set<TextChannel> channels, @NonNull final Map<User, Set<T>> userElements){
		userElements.entrySet().stream()
				.flatMap(entry -> entry.getValue().stream().map(val -> Map.entry(entry.getKey(), val)))
				.sorted(Map.Entry.comparingByValue())
				.forEach(change -> channels.stream()
						.filter(channel -> this.sendToChannel(channel, change.getKey()))
						.forEach(channel -> {
							var embed = this.buildMessage(channel.getGuild(), change.getKey(), change.getValue());
							Actions.sendEmbed(channel, embed);
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
	
	default boolean sendToChannel(final TextChannel channel, final User user){
		return Settings.getGeneral().getAniList().isRegisteredOn(channel.getGuild(), user);
	}
	
	@NonNull
	default Set<T> getElements(@NonNull final Member member) throws Exception{
		Log.getLogger(member.getGuild()).debug("Fetching user {}", member);
		var elementList = this.initQuery(member).getResult(member);
		if(!this.isKeepOnlyNew()){
			return elementList;
		}
		var aniListGeneral = Settings.getGeneral().getAniList();
		final var baseDate = aniListGeneral.getLastAccess(this.getFetcherID(), member.getUser().getIdLong())
				.map(UserDateConfiguration::getDate);
		elementList = elementList.stream()
				.filter(e -> e instanceof IAnilistDatedObject)
				.filter(e -> baseDate.map(date -> ((IAnilistDatedObject) e).getDate().isAfter(date)).orElse(true))
				.collect(Collectors.toSet());
		elementList.stream().filter(e -> e instanceof IAnilistDatedObject)
				.map(e -> (IAnilistDatedObject) e)
				.map(IAnilistDatedObject::getDate)
				.max(ZonedDateTime::compareTo)
				.ifPresent(val -> {
					Log.getLogger(member.getGuild()).debug("New last fetched date for {} on section {}: {} (last was {})", member, this.getFetcherID(), val, baseDate);
					aniListGeneral.setLastAccess(member.getUser(), this.getFetcherID(), val);
				});
		return elementList;
	}
	
	@NonNull U initQuery(@NonNull Member member);
	
	boolean isKeepOnlyNew();
	
	@NonNull JDA getJda();
	
	@NonNull
	default MessageEmbed buildMessage(@NonNull Guild guild, final User user, @NonNull final T change){
		final var builder = new EmbedBuilder();
		try{
			if(Objects.isNull(user)){
				builder.setAuthor(this.getJda().getSelfUser().getName(), change.getUrl().toString(), this.getJda().getSelfUser().getAvatarUrl());
			}
			else{
				builder.setAuthor(user.getName(), change.getUrl().toString(), user.getAvatarUrl());
			}
			change.fillEmbed(guild, builder);
		}
		catch(final Exception e){
			Log.getLogger(null).error("Error building message for {}", this.getName(), e);
			builder.addField("Error", e.getClass().getName() + " => " + e.getMessage(), false);
			builder.setColor(Color.RED);
		}
		return builder.build();
	}
	
	default Set<Member> getMembers(){
		return this.getJda().getGuilds().stream()
				.flatMap(guild -> Settings.getGeneral().getAniList().getRegisteredMembers(guild).stream())
				.collect(Collectors.toSet());
	}
}