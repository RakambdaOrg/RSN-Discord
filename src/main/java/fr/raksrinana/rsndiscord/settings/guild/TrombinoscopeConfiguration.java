package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.CompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.trombinoscope.Picture;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import java.io.File;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class TrombinoscopeConfiguration implements CompositeConfiguration{
	@JsonProperty("picturesChannel")
	@Setter
	private ChannelConfiguration picturesChannel;
	@JsonProperty("posterRole")
	@Setter
	private RoleConfiguration posterRole;
	@JsonProperty("pictures")
	private final Map<Long, Set<Picture>> pictures = new HashMap<>();
	@JsonProperty("bannedUsers")
	private final Set<UserConfiguration> bannedUsers = new HashSet<>();
	
	public void registerPicture(@NonNull User user, @NonNull File file){
		pictures.computeIfAbsent(user.getIdLong(), key -> new HashSet<>())
				.add(new Picture(Paths.get(file.toURI()), ZonedDateTime.now()));
	}
	
	@NonNull
	public Set<Picture> getPictures(@NonNull User user){
		return getPictures(user.getIdLong()).orElse(Set.of());
	}
	
	@NonNull
	public Optional<Set<Picture>> getPictures(long userId){
		return Optional.ofNullable(pictures.get(userId));
	}
	
	@NonNull
	public Optional<Long> getUserIdOfPicture(UUID uuid){
		return pictures.entrySet().stream()
				.filter(entry -> entry.getValue().stream().anyMatch(picture -> Objects.equals(picture.getUuid(), uuid)))
				.findFirst()
				.map(Map.Entry::getKey);
	}
	
	public void removePicture(long userId, UUID uuid){
		getPictures(userId).ifPresent(pictures -> pictures.removeIf(picture -> Objects.equals(picture.getUuid(), uuid)));
	}
	
	public boolean isUserPresent(long userId){
		return getPictures(userId).map(pictures -> !pictures.isEmpty()).orElse(false);
	}
	
	public void removeUser(@NonNull User user){
		this.pictures.remove(user.getIdLong());
	}
	
	public void banUser(@NonNull User user){
		this.bannedUsers.add(new UserConfiguration(user));
	}
	
	public void unbanUser(@NonNull User user){
		this.bannedUsers.remove(new UserConfiguration(user));
	}
	
	public boolean isUserBanned(@NonNull User user){
		return this.bannedUsers.contains(new UserConfiguration(user));
	}
	
	public int getUserCount(){
		return getPictures().size();
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getPicturesChannel(){
		return Optional.ofNullable(this.picturesChannel);
	}
	
	@NonNull
	public Optional<RoleConfiguration> getPosterRole(){
		return Optional.ofNullable(this.posterRole);
	}
}
