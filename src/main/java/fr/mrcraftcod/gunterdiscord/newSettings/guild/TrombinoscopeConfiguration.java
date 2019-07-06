package fr.mrcraftcod.gunterdiscord.newSettings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.newSettings.guild.trombinoscope.PhotoEntryConfiguration;
import fr.mrcraftcod.gunterdiscord.newSettings.types.ChannelConfiguration;
import fr.mrcraftcod.gunterdiscord.newSettings.types.RoleConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.PhotoChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.PhotoConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.TrombinoscopeRoleConfig;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrombinoscopeConfiguration{
	@JsonProperty("photoChannel")
	private ChannelConfiguration photoChannel;
	@JsonProperty("participantRole")
	private RoleConfiguration participantRole;
	@JsonProperty("photos")
	private List<PhotoEntryConfiguration> photos = new ArrayList<>();
	
	public void mapOldConf(Guild guild){
		this.photoChannel = new PhotoChannelConfig(guild).getObject().map(ChannelConfiguration::new).orElse(null);
		this.participantRole = new TrombinoscopeRoleConfig(guild).getObject().map(RoleConfiguration::new).orElse(null);
		this.photos.addAll(new PhotoConfig(guild).getAsMap().orElse(new HashMap<>()).entrySet().stream().flatMap(entry -> entry.getValue().stream().map(pic -> new PhotoEntryConfiguration(guild.getJDA().getUserById(entry.getKey()), pic))).collect(Collectors.toList()));
	}
	
	@Nonnull
	public Optional<RoleConfiguration> getParticipantRole(){
		return Optional.ofNullable(this.participantRole);
	}
	
	public void setParticipantRole(@Nullable RoleConfiguration value){
		this.participantRole = value;
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getPhotoChannel(){
		return Optional.ofNullable(this.photoChannel);
	}
	
	public void setPhotoChannel(@Nullable ChannelConfiguration value){
		this.photoChannel = value;
	}
}
