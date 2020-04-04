package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.CompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.anilist.AnilistAccessTokenConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserDateConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class AniListConfiguration implements CompositeConfiguration{
	@JsonProperty("accessToken")
	private final Set<AnilistAccessTokenConfiguration> tokens = new HashSet<>();
	@JsonProperty("notificationsChannel")
	@Setter
	private ChannelConfiguration notificationsChannel;
	@JsonProperty("mediaChangeChannel")
	@Setter
	private ChannelConfiguration mediaChangeChannel;
	@JsonProperty("refreshTokens")
	private final Map<Long, String> refreshTokens = new HashMap<>();
	@JsonProperty("lastAccess")
	@Getter
	private final Map<String, Set<UserDateConfiguration>> lastAccess = new HashMap<>();
	@JsonProperty("thaChannel")
	@Setter
	private ChannelConfiguration thaChannel;
	@JsonProperty("thaUser")
	@Setter
	private UserConfiguration thaUser;
	@JsonProperty("userIds")
	private final Map<Long, Integer> userIds = new HashMap<>();
	
	@NonNull
	public Optional<String> getRefreshToken(final long userId){
		return Optional.ofNullable(this.refreshTokens.getOrDefault(userId, null));
	}
	
	public void setRefreshToken(final long userId, @NonNull final String refreshToken){
		this.refreshTokens.put(userId, refreshToken);
	}
	
	public void setLastAccess(final User user, final String section, final LocalDateTime date){
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
	public Optional<AnilistAccessTokenConfiguration> getAccessToken(final long userId){
		final var now = LocalDateTime.now();
		return this.tokens.stream().filter(t -> Objects.equals(t.getUserId(), userId)).filter(t -> t.getExpireDate().isAfter(now)).sorted(Comparator.comparing(AnilistAccessTokenConfiguration::getExpireDate).reversed()).findAny();
	}
	
	public void setUserId(final long userId, final int aniListUserId){
		this.userIds.put(userId, aniListUserId);
	}
	
	public Optional<Integer> getUserId(final long userId){
		return Optional.ofNullable(this.userIds.get(userId));
	}
	
	public void addAccessToken(@NonNull final AnilistAccessTokenConfiguration value){
		this.tokens.add(value);
	}
	
	public void removeUser(@NonNull final User user){
		this.tokens.removeIf(t -> Objects.equals(t.getUserId(), user.getIdLong()));
		this.refreshTokens.remove(user.getIdLong());
		this.lastAccess.values().forEach(l -> l.removeIf(v -> Objects.equals(v.getUser().getUserId(), user.getIdLong())));
		this.userIds.remove(user.getIdLong());
	}
	
	public Set<Member> getRegisteredMembers(@NonNull Guild guild){
		return this.tokens.stream().map(token -> guild.retrieveMemberById(token.getUserId())).map(RestAction::complete).filter(Objects::nonNull).collect(Collectors.toSet());
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getMediaChangeChannel(){
		return Optional.ofNullable(this.mediaChangeChannel);
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getNotificationsChannel(){
		return Optional.ofNullable(this.notificationsChannel);
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getThaChannel(){
		return Optional.ofNullable(this.thaChannel);
	}
	
	@NonNull
	public Optional<UserConfiguration> getThaUser(){
		return Optional.ofNullable(this.thaUser);
	}
}
