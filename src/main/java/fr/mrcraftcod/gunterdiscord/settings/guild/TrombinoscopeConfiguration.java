package fr.mrcraftcod.gunterdiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.settings.guild.trombinoscope.PhotoEntryConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.RoleConfiguration;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("FieldMayBeFinal")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrombinoscopeConfiguration{
	@JsonProperty("photoChannel")
	private ChannelConfiguration photoChannel;
	@JsonProperty("participantRole")
	private RoleConfiguration participantRole;
	@JsonProperty("photos")
	private Set<PhotoEntryConfiguration> photos = new HashSet<>();
	
	public List<PhotoEntryConfiguration> getPhotos(final User user){
		return this.photos.stream().filter(p -> Objects.equals(p.getUserId(), user.getIdLong())).collect(Collectors.toList());
	}
	
	public void removePhoto(@Nonnull final User user, @Nonnull final String photo){
		this.photos.removeIf(p -> Objects.equals(p.getUserId(), user.getIdLong()) && Objects.equals(p.getPhoto(), photo));
	}
	
	public void removePhoto(@Nonnull final User user){
		this.photos.removeIf(p -> Objects.equals(p.getUserId(), user.getIdLong()));
	}
	
	@Nonnull
	public Optional<RoleConfiguration> getParticipantRole(){
		return Optional.ofNullable(this.participantRole);
	}
	
	public void setParticipantRole(@Nullable final RoleConfiguration value){
		this.participantRole = value;
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getPhotoChannel(){
		return Optional.ofNullable(this.photoChannel);
	}
	
	public void setPhotoChannel(@Nullable final ChannelConfiguration value){
		this.photoChannel = value;
	}
}
