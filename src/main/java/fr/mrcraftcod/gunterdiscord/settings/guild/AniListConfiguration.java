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
	private List<AnilistAccessTokenConfiguration> tokens = new ArrayList<>();
	@JsonProperty("notificationsChannel")
	private ChannelConfiguration notificationsChannel;
	@JsonProperty("refreshTokens")
	private Map<Long, String> refreshTokens = new HashMap<>();
	@JsonProperty("lastAccess")
	private Map<String, List<UserDateConfiguration>> lastAccess = new HashMap<>();
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
	
	public void setLastAccess(User user, String section, Date date){
		this.getLastAccess(section, user.getIdLong()).ifPresentOrElse(lastAccess -> lastAccess.setDate(date), () -> this.lastAccess.computeIfAbsent(section, sec -> new ArrayList<>()).add(new UserDateConfiguration(user, date)));
	}
	
	@Nonnull
	public Optional<UserDateConfiguration> getLastAccess(@Nonnull final String section, final long userId){
		return getLastAccess(section).stream().filter(lastAccess -> Objects.equals(lastAccess.getUserId(), userId)).findAny();
	}
	
	@Nonnull
	public List<UserDateConfiguration> getLastAccess(@Nonnull final String section){
		return Optional.ofNullable(this.lastAccess.get(section)).orElse(List.of());
	}
	
	@Nonnull
	public Optional<AnilistAccessTokenConfiguration> getAccessToken(long userId){
		return this.tokens.stream().filter(t -> Objects.equals(t.getUserId(), userId)).filter(t -> t.getExpireDate().after(new Date())).sorted(Comparator.comparing(AnilistAccessTokenConfiguration::getExpireDate).reversed()).findAny();
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
	
	@Nonnull
	public Map<String, List<UserDateConfiguration>> getLastAccess(){
		return this.lastAccess;
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getNotificationsChannel(){
		return Optional.ofNullable(this.notificationsChannel);
	}
	
	public void setNotificationsChannel(@Nullable final ChannelConfiguration channel){
		this.notificationsChannel = channel;
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
