package fr.mrcraftcod.gunterdiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.guild.anilist.AnilistAccessTokenConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.UserConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.UserDateConfiguration;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-06-23.
 *
 * @author Thomas Couchoud
 * @since 2019-06-23
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AniListConfiguration{
	@JsonProperty("accessToken")
	private Set<AnilistAccessTokenConfiguration> tokens = new HashSet<>();
	@JsonProperty("notificationsChannel")
	private ChannelConfiguration notificationsChannel;
	@JsonProperty("mediaChangeChannel")
	private ChannelConfiguration mediaChangeChannel;
	@JsonProperty("refreshTokens")
	private Map<Long, String> refreshTokens = new HashMap<>();
	@JsonProperty("lastAccess")
	private Map<String, Set<UserDateConfiguration>> lastAccess = new HashMap<>();
	@JsonProperty("thaChannel")
	private ChannelConfiguration thaChannel;
	@JsonProperty("thaUser")
	private UserConfiguration thaUser;
	@JsonProperty("userIds")
	private Map<Long, Integer> userIds = new HashMap<>();
	
	public AniListConfiguration(){
	}
	
	@Nonnull
	public Optional<String> getRefreshToken(final long userId){
		return Optional.ofNullable(this.refreshTokens.getOrDefault(userId, null));
	}
	
	public void setRefreshToken(final long userId, @Nonnull final String refreshToken){
		this.refreshTokens.put(userId, refreshToken);
	}
	
	public void setLastAccess(User user, String section, LocalDateTime date){
		this.getLastAccess(section, user.getIdLong()).ifPresentOrElse(lastAccess -> lastAccess.setDate(date), () -> this.lastAccess.computeIfAbsent(section, sec -> new HashSet<>()).add(new UserDateConfiguration(user, date)));
	}
	
	@Nonnull
	public Optional<UserDateConfiguration> getLastAccess(@Nonnull final String section, final long userId){
		return getLastAccess(section).stream().filter(lastAccess -> Objects.equals(lastAccess.getUserId(), userId)).findAny();
	}
	
	@Nonnull
	public Set<UserDateConfiguration> getLastAccess(@Nonnull final String section){
		return Optional.ofNullable(this.lastAccess.get(section)).orElse(Set.of());
	}
	
	@Nonnull
	public Optional<AnilistAccessTokenConfiguration> getAccessToken(long userId){
		final var now = LocalDateTime.now();
		return this.tokens.stream().filter(t -> Objects.equals(t.getUserId(), userId)).filter(t -> t.getExpireDate().isAfter(now)).sorted(Comparator.comparing(AnilistAccessTokenConfiguration::getExpireDate).reversed()).findAny();
	}
	
	public void setUserId(long userId, int aniListUserId){
		this.userIds.put(userId, aniListUserId);
	}
	
	public Optional<Integer> getUserId(long userId){
		return Optional.ofNullable(this.userIds.get(userId));
	}
	
	public void addAccessToken(@Nonnull AnilistAccessTokenConfiguration value){
		this.tokens.add(value);
	}
	
	public void removeUser(@Nonnull User user){
		tokens.removeIf(t -> Objects.equals(t.getUserId(), user.getIdLong()));
		refreshTokens.remove(user.getIdLong());
		lastAccess.values().forEach(l -> l.removeIf(v -> Objects.equals(v.getUserId(), user.getIdLong())));
		userIds.remove(user.getIdLong());
	}
	
	@Nonnull
	public Map<String, Set<UserDateConfiguration>> getLastAccess(){
		return this.lastAccess;
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getNotificationsChannel(){
		return Optional.ofNullable(this.notificationsChannel);
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getMediaChangeChannel(){
		return Optional.ofNullable(this.mediaChangeChannel);
	}
	
	public void setNotificationsChannel(@Nullable final ChannelConfiguration channel){
		this.notificationsChannel = channel;
	}
	
	public void setMediaChangeChannel(@Nullable final ChannelConfiguration channel){
		this.mediaChangeChannel = channel;
	}
	
	@Nonnull
	public List<User> getRegisteredUsers(){
		return this.refreshTokens.keySet().stream().map(userId -> Main.getJDA().getUserById(userId)).collect(Collectors.toList());
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getThaChannel(){
		return Optional.ofNullable(this.thaChannel);
	}
	
	public void setThaChannel(final ChannelConfiguration channel){
		this.thaChannel = channel;
	}
	
	@Nonnull
	public Optional<UserConfiguration> getThaUser(){
		return Optional.ofNullable(this.thaUser);
	}
	
	public void setThaUser(final UserConfiguration user){
		this.thaUser = user;
	}
}
