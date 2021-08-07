package fr.raksrinana.rsndiscord.settings.impl.general;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.impl.general.anilist.AniListAccessTokenConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
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
public class AniListGeneral{
	@JsonProperty("accessToken")
	private final Set<AniListAccessTokenConfiguration> tokens = new HashSet<>();
	@JsonProperty("refreshTokens")
	private final Map<Long, String> refreshTokens = new HashMap<>();
	@JsonProperty("lastAccess")
	@Getter
	private final Map<String, Set<UserDateConfiguration>> lastAccess = new HashMap<>();
	@JsonProperty("userIds")
	private final Map<Long, Integer> userIds = new HashMap<>();
	
	@NotNull
	public Optional<String> getRefreshToken(long userId){
		return ofNullable(refreshTokens.getOrDefault(userId, null));
	}
	
	public void setRefreshToken(long userId, @NotNull String refreshToken){
		refreshTokens.put(userId, refreshToken);
	}
	
	public void setLastAccess(@NotNull User user, @NotNull String section, @NotNull ZonedDateTime date){
		getLastAccess(section, user.getIdLong())
				.ifPresentOrElse(lastAccess -> lastAccess.setDate(date),
						() -> lastAccess.computeIfAbsent(section, sec -> new HashSet<>()).add(new UserDateConfiguration(user, date)));
	}
	
	@NotNull
	public Optional<UserDateConfiguration> getLastAccess(@NotNull String section, long userId){
		return getLastAccess(section).stream()
				.filter(lastAccess -> Objects.equals(lastAccess.getUser().getUserId(), userId))
				.findAny();
	}
	
	@NotNull
	public Set<UserDateConfiguration> getLastAccess(@NotNull String section){
		return ofNullable(lastAccess.get(section)).orElse(Set.of());
	}
	
	@NotNull
	public Optional<AniListAccessTokenConfiguration> getAccessToken(long userId){
		var now = ZonedDateTime.now();
		return tokens.stream().filter(t -> Objects.equals(t.getUserId(), userId))
				.filter(t -> t.getExpireDate().isAfter(now))
				.sorted(comparing(AniListAccessTokenConfiguration::getExpireDate).reversed())
				.findAny();
	}
	
	public void setUserId(long userId, int aniListUserId){
		userIds.put(userId, aniListUserId);
	}
	
	@NotNull
	public Optional<Integer> getUserId(long userId){
		return ofNullable(userIds.get(userId));
	}
	
	public void addAccessToken(@NotNull AniListAccessTokenConfiguration value){
		tokens.add(value);
	}
	
	public void removeUser(@NotNull User user){
		tokens.removeIf(t -> Objects.equals(t.getUserId(), user.getIdLong()));
		refreshTokens.remove(user.getIdLong());
		lastAccess.values().forEach(l -> l.removeIf(v -> Objects.equals(v.getUser().getUserId(), user.getIdLong())));
		userIds.remove(user.getIdLong());
	}
	
	@NotNull
	public Set<Member> getRegisteredMembers(@NotNull Guild guild){
		return tokens.stream().map(token -> guild.retrieveMemberById(token.getUserId()))
				.map(RestAction::complete)
				.filter(Objects::nonNull)
				.collect(toSet());
	}
	
	public boolean isRegisteredOn(@NotNull Guild guild, @NotNull User user){
		return true;
	}
}
