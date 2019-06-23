package fr.mrcraftcod.gunterdiscord.settings.newConfigs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListAccessTokenConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListCodeConfig;
import fr.mrcraftcod.gunterdiscord.settings.newConfigs.anilist.AnilistAccessTokenConfiguration;
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
public class AnilistConfiguration{
	@JsonProperty("accessToken")
	private List<AnilistAccessTokenConfiguration> tokens = new ArrayList<>();
	@JsonProperty("notificationsChannel")
	private ChannelConfiguration notificationsChannel;
	@JsonProperty("refreshTokens")
	private Map<Long, String> refreshTokens = new HashMap<>();
	@JsonProperty("lastAccess")
	private Map<String, List<UserDateConfiguration>> lastAccess = new HashMap<>();
	
	public void mapOldConf(@Nonnull Guild guild){
		new AniListAccessTokenConfig(guild).getAsMap().ifPresent(map -> map.forEach((user, tokens) -> tokens.forEach((date, token) -> AnilistConfiguration.this.tokens.add(new AnilistAccessTokenConfiguration(user, new Date(date), token)))));
		new AniListChannelConfig(guild).getObject().ifPresent(channel -> this.notificationsChannel = new ChannelConfiguration(channel));
		new AniListCodeConfig(guild).getAsMap().ifPresent(map -> map.forEach((user, token) -> refreshTokens.put(user, token)));
	}
	
	@Nonnull
	public Optional<String> getRefreshToken(long userId){
		return Optional.ofNullable(this.refreshTokens.getOrDefault(userId, null));
	}
	
	public void setRefreshToken(long userId, @Nonnull String refreshToken){
		this.refreshTokens.put(userId, refreshToken);
	}
	
	public Optional<UserDateConfiguration> getLastAccess(@Nonnull String section, long userId){
		return getLastAccess(section).stream().filter(lastAccess -> Objects.equals(lastAccess.getUserId(), userId)).findAny();
	}
	
	public List<UserDateConfiguration> getLastAccess(@Nonnull String section){
		return Optional.ofNullable(this.lastAccess.get(section)).orElse(List.of());
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getNotificationsChannel(){
		return Optional.ofNullable(this.notificationsChannel);
	}
	
	public void setNotificationsChannel(@Nullable ChannelConfiguration channel){
		this.notificationsChannel = channel;
	}
}
