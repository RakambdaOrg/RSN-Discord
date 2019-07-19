package fr.mrcraftcod.gunterdiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.settings.guild.participation.EntityParticipation;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.types.UserConfiguration;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true, value = {
		"usersLock",
		"emotesLock"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipationConfig{
	@JsonIgnore
	private final Object emotesLock = new Object();
	@JsonIgnore
	private final Object usersLock = new Object();
	@JsonProperty("emotes")
	private Set<EntityParticipation> emotesParticipation = new HashSet<>();
	@JsonProperty("users")
	private Set<EntityParticipation> usersParticipation = new HashSet<>();
	@JsonProperty("usersPenned")
	private Set<UserConfiguration> usersPinned = new HashSet<>();
	@JsonProperty("reportChannel")
	private ChannelConfiguration reportChannel;
	
	public ParticipationConfig(){
	}
	
	public EntityParticipation getUsers(@Nonnull LocalDate date){
		return this.getUsers(date, true).orElseThrow();
	}
	
	public Optional<EntityParticipation> getUsers(@Nonnull LocalDate date, boolean create){
		synchronized(usersLock){
			return this.usersParticipation.stream().filter(p -> Objects.equals(date, p.getDate())).findFirst().or(() -> {
				if(create){
					final var p = new EntityParticipation(date);
					this.addUserParticipation(p);
					return Optional.of(p);
				}
				return Optional.empty();
			});
		}
	}
	
	private void addUserParticipation(@Nonnull EntityParticipation entityParticipation){
		this.usersParticipation.add(entityParticipation);
	}
	
	@Nonnull
	public EntityParticipation getEmotes(@Nonnull LocalDate date){
		return this.getEmotes(date, true).orElseThrow();
	}
	
	public void removeUsers(@Nonnull LocalDate date){
		this.usersParticipation.removeIf(p -> Objects.equals(p.getDate(), date));
	}
	
	public void removeEmotes(@Nonnull LocalDate date){
		this.emotesParticipation.removeIf(p -> Objects.equals(p.getDate(), date));
	}
	
	public Optional<EntityParticipation> getEmotes(LocalDate date, boolean create){
		synchronized(emotesLock){
			return this.emotesParticipation.stream().filter(p -> Objects.equals(date, p.getDate())).findFirst().or(() -> {
				if(create){
					final var p = new EntityParticipation(date);
					this.addEmoteParticipation(p);
					return Optional.of(p);
				}
				return Optional.empty();
			});
		}
	}
	
	private void addEmoteParticipation(@Nonnull EntityParticipation entityParticipation){
		this.emotesParticipation.add(entityParticipation);
	}
	
	public void removeEmotesBefore(@Nonnull LocalDate week){
		this.emotesParticipation.removeIf(p -> p.getDate().isBefore(week));
	}
	
	public void removeUsersBefore(LocalDate date){
		this.usersParticipation.removeIf(p -> p.getDate().isBefore(date));
	}
	
	@Nonnull
	public Set<EntityParticipation> getEmotes(){
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
	public Set<UserConfiguration> getUsersPinned(){
		return this.usersPinned;
	}
	
	public void setUsersPinned(@Nonnull Set<UserConfiguration> usersPinned){
		this.usersPinned = usersPinned;
	}
}
