package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.CompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.trombinoscope.Picture;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
	private Map<Long, Set<Picture>> pictures = new HashMap<>();
	
	public void registerPicture(User user, File file){
		pictures.computeIfAbsent(user.getIdLong(), key -> new HashSet<>())
				.add(new Picture(Paths.get(file.toURI()), ZonedDateTime.now()));
	}
	
	public Set<Picture> getPictures(User user){
		return pictures.getOrDefault(user.getIdLong(), Set.of());
	}
	
	public Optional<ChannelConfiguration> getPicturesChannel(){
		return Optional.ofNullable(this.picturesChannel);
	}
	
	public Optional<RoleConfiguration> getPosterRole(){
		return Optional.ofNullable(this.posterRole);
	}
}
