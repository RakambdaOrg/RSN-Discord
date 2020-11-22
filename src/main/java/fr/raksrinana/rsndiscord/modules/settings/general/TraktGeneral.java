package fr.raksrinana.rsndiscord.modules.settings.general;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.modules.settings.general.trakt.TraktAccessTokenConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.types.UserDateConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import java.time.ZonedDateTime;
import java.util.*;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class TraktGeneral{
	@JsonProperty("accessToken")
	private final Set<TraktAccessTokenConfiguration> tokens = new HashSet<>();
	@JsonProperty("lastAccess")
	private final Map<String, Set<UserDateConfiguration>> lastAccess = new HashMap<>();
	@JsonProperty("usernames")
	private final Map<Long, String> usernames = new HashMap<>();
	
	public void setLastAccess(final User user, final String section, final ZonedDateTime date){
		this.getLastAccess(section, user.getIdLong())
				.ifPresentOrElse(lastAccess -> lastAccess.setDate(date),
						() -> this.lastAccess.computeIfAbsent(section, sec -> new HashSet<>()).add(new UserDateConfiguration(user, date)));
	}
	
	@NonNull
	public Optional<UserDateConfiguration> getLastAccess(@NonNull final String section, final long userId){
		return this.getLastAccess(section).stream().filter(lastAccess -> Objects.equals(lastAccess.getUser().getUserId(), userId)).findAny();
	}
	
	@NonNull
	public Set<UserDateConfiguration> getLastAccess(@NonNull final String section){
		return ofNullable(this.lastAccess.get(section)).orElse(Set.of());
	}
	
	@NonNull
	public Optional<TraktAccessTokenConfiguration> getAccessToken(final long userId){
		final var now = ZonedDateTime.now();
		return this.tokens.stream().filter(t -> Objects.equals(t.getUserId(), userId))
				.filter(t -> t.getExpireDate().isAfter(now))
				.sorted(comparing(TraktAccessTokenConfiguration::getExpireDate).reversed())
				.findAny();
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
	
	public void setUsername(final long userId, final String username){
		this.usernames.put(userId, username);
	}
	
	public Optional<String> getUserUsername(final long userId){
		return ofNullable(this.usernames.get(userId));
	}
	
	public boolean isRegisteredOn(@NonNull Guild guild, @NonNull User user){
		return true;
	}
	
	@NonNull
	public Set<User> getRegisteredUsers(){
		return this.tokens.stream().map(TraktAccessTokenConfiguration::getUserId)
				.map(userId -> Main.getJda().retrieveUserById(userId).complete())
				.filter(Objects::nonNull)
				.collect(toSet());
	}
}
