package fr.raksrinana.rsndiscord.settings.general;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.general.trakt.TraktAccessTokenConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
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
	
	public void setLastAccess(@NotNull User user, @NotNull String section, @NotNull ZonedDateTime date){
		getLastAccess(section, user.getIdLong())
				.ifPresentOrElse(lastAccess -> lastAccess.setDate(date),
						() -> lastAccess.computeIfAbsent(section, sec -> new HashSet<>()).add(new UserDateConfiguration(user, date)));
	}
	
	@NotNull
	public Optional<UserDateConfiguration> getLastAccess(@NotNull String section, long userId){
		return getLastAccess(section).stream().filter(lastAccess -> Objects.equals(lastAccess.getUser().getUserId(), userId)).findAny();
	}
	
	@NotNull
	public Set<UserDateConfiguration> getLastAccess(@NotNull String section){
		return ofNullable(lastAccess.get(section)).orElse(Set.of());
	}
	
	@NotNull
	public Optional<TraktAccessTokenConfiguration> getAccessToken(long userId){
		var now = ZonedDateTime.now();
		return tokens.stream().filter(t -> Objects.equals(t.getUserId(), userId))
				.filter(t -> t.getExpireDate().isAfter(now))
				.sorted(comparing(TraktAccessTokenConfiguration::getExpireDate).reversed())
				.findAny();
	}
	
	public void addAccessToken(@NotNull TraktAccessTokenConfiguration value){
		tokens.add(value);
	}
	
	public void removeUser(@NotNull User user){
		tokens.removeIf(t -> Objects.equals(t.getUserId(), user.getIdLong()));
		lastAccess.values().forEach(l -> l.removeIf(v -> Objects.equals(v.getUser().getUserId(), user.getIdLong())));
	}
	
	public void removeAccessToken(@NotNull TraktAccessTokenConfiguration value){
		tokens.remove(value);
	}
	
	public void setUsername(long userId, @NotNull String username){
		usernames.put(userId, username);
	}
	
	public Optional<String> getUserUsername(long userId){
		return ofNullable(usernames.get(userId));
	}
	
	public boolean isRegisteredOn(@NotNull Guild guild, @NotNull User user){
		return true;
	}
	
	@NotNull
	public Set<User> getRegisteredUsers(){
		return tokens.stream().map(TraktAccessTokenConfiguration::getUserId)
				.map(userId -> Main.getJda().retrieveUserById(userId).complete())
				.filter(Objects::nonNull)
				.collect(toSet());
	}
}
