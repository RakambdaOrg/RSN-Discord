package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.guild.trombinoscope.PhotoEntryConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class TrombinoscopeConfiguration{
	@JsonProperty("photoChannel")
	@Setter
	private ChannelConfiguration photoChannel;
	@JsonProperty("participantRole")
	@Setter
	private RoleConfiguration participantRole;
	@JsonProperty("photos")
	private Set<PhotoEntryConfiguration> photos = new HashSet<>();
	
	public List<PhotoEntryConfiguration> getPhotos(final User user){
		return this.photos.stream().filter(p -> Objects.equals(p.getUserId(), user.getIdLong())).collect(Collectors.toList());
	}
	
	public void removePhoto(@NonNull final User user, @NonNull final String photo){
		this.photos.removeIf(p -> Objects.equals(p.getUserId(), user.getIdLong()) && Objects.equals(p.getPhoto(), photo));
	}
	
	public void removePhoto(@NonNull final User user){
		this.photos.removeIf(p -> Objects.equals(p.getUserId(), user.getIdLong()));
	}
	
	@NonNull
	public Optional<RoleConfiguration> getParticipantRole(){
		return Optional.ofNullable(this.participantRole);
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getPhotoChannel(){
		return Optional.ofNullable(this.photoChannel);
	}
}
