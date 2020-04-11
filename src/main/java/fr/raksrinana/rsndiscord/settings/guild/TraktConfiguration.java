package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.CompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.trakt.TraktAccessTokenConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("FieldMayBeFinal")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@Slf4j
public class TraktConfiguration implements CompositeConfiguration{
	@JsonProperty("accessToken")
	private Set<TraktAccessTokenConfiguration> tokens = new HashSet<>();
	@JsonProperty("mediaChangeChannel")
	@Setter
	private ChannelConfiguration mediaChangeChannel;
	@JsonProperty("lastAccess")
	private Map<String, Set<UserDateConfiguration>> lastAccess = new HashMap<>();
	@JsonProperty("thaChannel")
	@Setter
	private ChannelConfiguration thaChannel;
	@JsonProperty("thaUser")
	@Setter
	private UserConfiguration thaUser;
	@JsonProperty("usernames")
	private Map<Long, String> usernames = new HashMap<>();
	
	public void setLastAccess(final User user, final String section, final ZonedDateTime date){
		this.getLastAccess(section, user.getIdLong()).ifPresentOrElse(lastAccess -> lastAccess.setDate(date), () -> this.lastAccess.computeIfAbsent(section, sec -> new HashSet<>()).add(new UserDateConfiguration(user, date)));
	}
	
	@NonNull
	public Optional<UserDateConfiguration> getLastAccess(@NonNull final String section, final long userId){
		return this.getLastAccess(section).stream().filter(lastAccess -> Objects.equals(lastAccess.getUser().getUserId(), userId)).findAny();
	}
	
	@NonNull
	public Set<UserDateConfiguration> getLastAccess(@NonNull final String section){
		return Optional.ofNullable(this.lastAccess.get(section)).orElse(Set.of());
	}
	
	@NonNull
	public Optional<TraktAccessTokenConfiguration> getAccessToken(final long userId){
		final var now = ZonedDateTime.now();
		return this.tokens.stream().filter(t -> Objects.equals(t.getUserId(), userId)).filter(t -> t.getExpireDate().isAfter(now)).sorted(Comparator.comparing(TraktAccessTokenConfiguration::getExpireDate).reversed()).findAny();
	}
	
	public void addAccessToken(@NonNull final TraktAccessTokenConfiguration value){
		this.tokens.add(value);
	}
	
	public void removeUser(@NonNull final User user){
		this.tokens.removeIf(t -> Objects.equals(t.getUserId(), user.getIdLong()));
		this.lastAccess.values().forEach(l -> l.removeIf(v -> Objects.equals(v.getUser().getUserId(), user.getIdLong())));
	}
	
	public void removeAccessToken(@NonNull TraktAccessTokenConfiguration value){
		this.tokens.remove(value);
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getMediaChangeChannel(){
		return Optional.ofNullable(this.mediaChangeChannel);
	}
	
	@NonNull
	public Set<User> getRegisteredUsers(){
		return this.tokens.stream().map(TraktAccessTokenConfiguration::getUserId).map(userId -> Main.getJda().retrieveUserById(userId).complete()).filter(Objects::nonNull).collect(Collectors.toSet());
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getThaChannel(){
		return Optional.ofNullable(this.thaChannel);
	}
	
	@NonNull
	public Optional<UserConfiguration> getThaUser(){
		return Optional.ofNullable(this.thaUser);
	}
	
	public void setUsername(final long userId, final String username){
		this.usernames.put(userId, username);
	}
	
	public Optional<String> getUserUsername(final long userId){
		return Optional.ofNullable(this.usernames.get(userId));
	}
}
