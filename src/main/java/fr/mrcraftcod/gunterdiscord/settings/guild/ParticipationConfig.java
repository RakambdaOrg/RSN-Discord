package fr.mrcraftcod.gunterdiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.settings.guild.participation.EntityParticipation;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.UserConfiguration;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipationConfig{
	@JsonProperty("emotes")
	private List<EntityParticipation> emotesParticipation = new ArrayList<>();
	@JsonProperty("users")
	private List<EntityParticipation> usersParticipation = new ArrayList<>();
	@JsonProperty("usersPenned")
	private List<UserConfiguration> usersPinned = new ArrayList<>();
	@JsonProperty("reportChannel")
	private ChannelConfiguration reportChannel;
	
	public ParticipationConfig(){
	}
	
	public void addEmoteParticipation(@Nonnull EntityParticipation entityParticipation){
		this.emotesParticipation.add(entityParticipation);
	}
	
	public Optional<EntityParticipation> getUsers(@Nonnull LocalDate date){
		return this.usersParticipation.stream().filter(p -> Objects.equals(date, p.getDate())).findFirst();
	}
	
	public void removeUsers(@Nonnull LocalDate date){
		this.usersParticipation.removeIf(p -> Objects.equals(p.getDate(), date));
	}
	
	public void removeEmotes(@Nonnull LocalDate date){
		this.emotesParticipation.removeIf(p -> Objects.equals(p.getDate(), date));
	}
	
	@Nonnull
	public Optional<EntityParticipation> getEmotes(@Nonnull LocalDate date){
		return this.emotesParticipation.stream().filter(p -> Objects.equals(date, p.getDate())).findFirst();
	}
	
	@Nonnull
	public List<EntityParticipation> getEmotes(){
		return this.emotesParticipation;
	}
	
	@Nonnull
	public Optional<ChannelConfiguration> getReportChannel(){
		return Optional.ofNullable(this.reportChannel);
	}
	
	public void setReportChannel(@Nullable ChannelConfiguration value){
		this.reportChannel = value;
	}
	
	@Nonnull
	public List<UserConfiguration> getUsersPinned(){
		return this.usersPinned;
	}
	
	public void setUsersPinned(@Nonnull List<UserConfiguration> usersPinned){
		this.usersPinned = usersPinned;
	}
}
