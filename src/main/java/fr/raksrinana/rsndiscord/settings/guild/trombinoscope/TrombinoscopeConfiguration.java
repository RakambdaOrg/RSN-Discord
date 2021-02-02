package fr.raksrinana.rsndiscord.settings.guild.trombinoscope;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.*;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class TrombinoscopeConfiguration implements ICompositeConfiguration{
	@JsonProperty("picturesChannel")
	@Setter
	private ChannelConfiguration picturesChannel;
	@JsonProperty("posterRole")
	@Setter
	private RoleConfiguration posterRole;
	@JsonProperty("pictures")
	private final Map<Long, Set<Picture>> pictures = new HashMap<>();
	
	public void registerPicture(@NotNull User user, @NotNull Path file){
		pictures.computeIfAbsent(user.getIdLong(), key -> new HashSet<>())
				.add(new Picture(file, ZonedDateTime.now()));
	}
	
	@NotNull
	public Set<Picture> getPictures(@NotNull User user){
		return getPictures(user.getIdLong()).orElse(Set.of());
	}
	
	@NotNull
	public Optional<Set<Picture>> getPictures(long userId){
		return ofNullable(pictures.get(userId));
	}
	
	@NotNull
	public Optional<Long> getUserIdOfPicture(@Nullable UUID uuid){
		return pictures.entrySet().stream()
				.filter(entry -> entry.getValue().stream().anyMatch(picture -> Objects.equals(picture.getUuid(), uuid)))
				.findFirst()
				.map(Map.Entry::getKey);
	}
	
	public void removePicture(long userId, @Nullable UUID uuid){
		getPictures(userId).ifPresent(pictures -> pictures.removeIf(picture -> Objects.equals(picture.getUuid(), uuid)));
	}
	
	public boolean isUserPresent(long userId){
		return getPictures(userId).map(pictures -> !pictures.isEmpty()).orElse(false);
	}
	
	public void removeUser(@NotNull User user){
		pictures.remove(user.getIdLong());
	}
	
	public int getUserCount(){
		return getPictures().size();
	}
	
	@NotNull
	public Optional<ChannelConfiguration> getPicturesChannel(){
		return ofNullable(picturesChannel);
	}
	
	@NotNull
	public Optional<RoleConfiguration> getPosterRole(){
		return ofNullable(posterRole);
	}
}
