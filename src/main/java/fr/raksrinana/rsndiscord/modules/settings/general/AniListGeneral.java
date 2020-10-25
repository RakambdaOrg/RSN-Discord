package fr.raksrinana.rsndiscord.modules.settings.general;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.modules.settings.general.anilist.AniListAccessTokenConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.types.UserDateConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
	
	@NonNull
	public Optional<String> getRefreshToken(final long userId){
		return Optional.ofNullable(this.refreshTokens.getOrDefault(userId, null));
	}
	
	public void setRefreshToken(final long userId, @NonNull final String refreshToken){
		this.refreshTokens.put(userId, refreshToken);
	}
	
	public void setLastAccess(final User user, final String section, final ZonedDateTime date){
		this.getLastAccess(section, user.getIdLong())
				.ifPresentOrElse(lastAccess -> lastAccess.setDate(date),
						() -> this.lastAccess.computeIfAbsent(section, sec -> new HashSet<>()).add(new UserDateConfiguration(user, date)));
	}
	
	@NonNull
	public Optional<UserDateConfiguration> getLastAccess(@NonNull final String section, final long userId){
		return this.getLastAccess(section).stream()
				.filter(lastAccess -> Objects.equals(lastAccess.getUser().getUserId(), userId))
				.findAny();
	}
	
	@NonNull
	public Set<UserDateConfiguration> getLastAccess(@NonNull final String section){
		return Optional.ofNullable(this.lastAccess.get(section)).orElse(Set.of());
	}
	
	@NonNull
	public Optional<AniListAccessTokenConfiguration> getAccessToken(final long userId){
		final var now = ZonedDateTime.now();
		return this.tokens.stream().filter(t -> Objects.equals(t.getUserId(), userId))
				.filter(t -> t.getExpireDate().isAfter(now))
				.sorted(Comparator.comparing(AniListAccessTokenConfiguration::getExpireDate).reversed())
				.findAny();
	}
	
	public void setUserId(final long userId, final int aniListUserId){
		this.userIds.put(userId, aniListUserId);
	}
	
	public Optional<Integer> getUserId(final long userId){
		return Optional.ofNullable(this.userIds.get(userId));
	}
	
	public void addAccessToken(@NonNull final AniListAccessTokenConfiguration value){
		this.tokens.add(value);
	}
	
	public void removeUser(@NonNull final User user){
		this.tokens.removeIf(t -> Objects.equals(t.getUserId(), user.getIdLong()));
		this.refreshTokens.remove(user.getIdLong());
		this.lastAccess.values().forEach(l -> l.removeIf(v -> Objects.equals(v.getUser().getUserId(), user.getIdLong())));
		this.userIds.remove(user.getIdLong());
	}
	
	public Set<Member> getRegisteredMembers(@NonNull Guild guild){
		return this.tokens.stream().map(token -> guild.retrieveMemberById(token.getUserId()))
				.map(RestAction::complete)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}
	
	public boolean isRegisteredOn(@NonNull Guild guild, @NonNull User user){
		return true;
	}
}
