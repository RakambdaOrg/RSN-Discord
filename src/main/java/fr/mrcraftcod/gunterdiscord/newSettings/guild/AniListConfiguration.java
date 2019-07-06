package fr.mrcraftcod.gunterdiscord.newSettings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.newSettings.types.ChannelConfiguration;
import fr.mrcraftcod.gunterdiscord.newSettings.types.UserConfiguration;
import fr.mrcraftcod.gunterdiscord.newSettings.types.UserDateConfiguration;
import fr.mrcraftcod.gunterdiscord.newSettings.guild.anilist.AnilistAccessTokenConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.*;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

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
	
	public AniListConfiguration(){
	}
	
	public void mapOldConf(@Nonnull final Guild guild){
		new AniListAccessTokenConfig(guild).getAsMap().ifPresent(map -> map.forEach((user, tokens) -> tokens.forEach((date, token) -> AniListConfiguration.this.tokens.add(new AnilistAccessTokenConfiguration(user, new Date(date), token)))));
		new AniListChannelConfig(guild).getObject().ifPresent(channel -> this.notificationsChannel = new ChannelConfiguration(channel));
		new AniListCodeConfig(guild).getAsMap().ifPresent(map -> map.forEach((user, token) -> this.refreshTokens.put(user, token)));
		new AnilistThaChannelConfig(guild).getObject().ifPresent(channel -> this.thaChannel = new ChannelConfiguration(channel));
		new AnilistThaUserConfig(guild).getObject().ifPresent(user -> this.thaUser = new UserConfiguration(user.getUser().getIdLong()));
	}
	
	@Nonnull
	public Optional<String> getRefreshToken(final long userId){
		return Optional.ofNullable(this.refreshTokens.getOrDefault(userId, null));
	}
	
	public void setRefreshToken(final long userId, @Nonnull final String refreshToken){
		this.refreshTokens.put(userId, refreshToken);
	}
	
	public Optional<UserDateConfiguration> getLastAccess(@Nonnull final String section, final long userId){
		return getLastAccess(section).stream().filter(lastAccess -> Objects.equals(lastAccess.getUserId(), userId)).findAny();
	}
	
	public List<UserDateConfiguration> getLastAccess(@Nonnull final String section){
		return Optional.ofNullable(this.lastAccess.get(section)).orElse(List.of());
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getNotificationsChannel(){
		return Optional.ofNullable(this.notificationsChannel);
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
	
	public void setNotificationsChannel(@Nullable final ChannelConfiguration channel){
		this.notificationsChannel = channel;
	}
}
