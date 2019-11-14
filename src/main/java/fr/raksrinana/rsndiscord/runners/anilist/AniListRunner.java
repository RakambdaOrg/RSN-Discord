package fr.raksrinana.rsndiscord.runners.anilist;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.anilist.AniListObject;
import fr.raksrinana.rsndiscord.utils.anilist.DatedObject;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public interface AniListRunner<T extends AniListObject, U extends PagedQuery<T>>{
	default void runQueryOnDefaultUsersChannels(){
		final var channels = this.getChannels();
		final var members = this.getMembers();
		this.runQuery(members, channels);
	}
	
	Set<TextChannel> getChannels();
	
	default Set<Member> getMembers(){
		return this.getChannels().stream().flatMap(channel -> Settings.get(channel.getGuild()).getAniListConfiguration().getRegisteredUsers().stream().map(user -> channel.getGuild().getMember(user))).collect(Collectors.toSet());
	}
	
	default void runQuery(@NonNull final Set<Member> members, @NonNull final Set<TextChannel> channels){
		Log.getLogger(null).info("Starting AniList {} runner", this.getRunnerName());
		try{
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
			Log.getLogger(null).info("AniList {} runner done", this.getRunnerName());
		}
		catch(final Exception e){
			Log.getLogger(null).error("Error in AniList {} runner", this.getRunnerName(), e);
		}
	}
	
	@NonNull String getRunnerName();
	
	@NonNull
	default Set<T> getElements(@NonNull final Member member) throws Exception{
		Log.getLogger(member.getGuild()).debug("Fetching user {}", member);
		var elementList = this.initQuery(member).getResult(member);
		if(this.isKeepOnlyNew()){
			final var baseDate = Settings.get(member.getGuild()).getAniListConfiguration().getLastAccess(this.getRunnerName(), member.getUser().getIdLong()).map(UserDateConfiguration::getDate).orElse(LocalDateTime.of(2019, 7, 7, 0, 0));
			elementList = elementList.stream().filter(e -> e instanceof DatedObject).filter(e -> ((DatedObject) e).getDate().isAfter(baseDate)).collect(Collectors.toSet());
		}
		elementList.stream().filter(e -> e instanceof DatedObject).map(e -> (DatedObject) e).map(DatedObject::getDate).max(LocalDateTime::compareTo).ifPresent(val -> {
			Log.getLogger(member.getGuild()).debug("New last fetched date for {} on section {}: {}", member, this.getRunnerName(), val);
			Settings.get(member.getGuild()).getAniListConfiguration().setLastAccess(member.getUser(), this.getRunnerName(), val);
		});
		return elementList;
	}
	
	default void sendMessages(@NonNull final Set<TextChannel> channels, @NonNull final Map<User, Set<T>> userElements){
		if(this.isSortedByUser()){
			for(final var entry : userElements.entrySet()){
				final var user = entry.getKey();
				entry.getValue().stream().sorted().map(change -> this.buildMessage(user, change)).forEach(embed -> channels.stream().filter(channel -> this.sendToChannel(channel, user)).forEach(channel -> Actions.sendMessage(channel, "", embed)));
			}
		}
		else{
			userElements.entrySet().stream().flatMap(es -> es.getValue().stream().map(val -> Map.entry(es.getKey(), val))).sorted(Map.Entry.comparingByValue()).map(change -> Map.entry(change.getKey(), this.buildMessage(change.getKey(), change.getValue()))).forEach(infos -> channels.stream().filter(chan -> this.sendToChannel(chan, infos.getKey())).forEach(chan -> Actions.sendMessage(chan, "", infos.getValue())));
		}
	}
	
	@NonNull U initQuery(@NonNull Member member);
	
	boolean isKeepOnlyNew();
	
	default boolean isSortedByUser(){
		return false;
	}
	
	@NonNull
	default MessageEmbed buildMessage(final User user, @NonNull final T change){
		final var builder = new EmbedBuilder();
		try{
			if(Objects.isNull(user)){
				builder.setAuthor(this.getJda().getSelfUser().getName(), change.getUrl().toString(), this.getJda().getSelfUser().getAvatarUrl());
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
		return Settings.get(channel.getGuild()).getAniListConfiguration().getAccessToken(user.getIdLong()).isPresent();
	}
	
	@NonNull JDA getJda();
	
	@NonNull String getFetcherID();
}
