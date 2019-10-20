package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.guild.participation.EntityParticipation;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("FieldMayBeFinal")
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
	
	public EntityParticipation getUsers(@Nonnull final LocalDate date){
		return this.getUsers(date, true).orElseThrow();
	}
	
	public Optional<EntityParticipation> getUsers(@Nonnull final LocalDate date, final boolean create){
		synchronized(this.usersLock){
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
	
	private void addUserParticipation(@Nonnull final EntityParticipation entityParticipation){
		this.usersParticipation.add(entityParticipation);
	}
	
	@Nonnull
	public EntityParticipation getEmotes(@Nonnull final LocalDate date){
		return this.getEmotes(date, true).orElseThrow();
	}
	
	public Optional<EntityParticipation> getEmotes(final LocalDate date, final boolean create){
		synchronized(this.emotesLock){
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
	
	private void addEmoteParticipation(@Nonnull final EntityParticipation entityParticipation){
		this.emotesParticipation.add(entityParticipation);
	}
	
	public void removeUsers(@Nonnull final LocalDate date){
		this.usersParticipation.removeIf(p -> Objects.equals(p.getDate(), date));
	}
	
	public void removeEmotes(@Nonnull final LocalDate date){
		this.emotesParticipation.removeIf(p -> Objects.equals(p.getDate(), date));
	}
	
	public void removeEmotesBefore(@Nonnull final LocalDate week){
		this.emotesParticipation.removeIf(p -> p.getDate().isBefore(week));
	}
	
	public void removeUsersBefore(final LocalDate date){
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
	
	public void setReportChannel(@Nullable final ChannelConfiguration value){
		this.reportChannel = value;
	}
	
	@Nonnull
	public Set<UserConfiguration> getUsersPinned(){
		return this.usersPinned;
	}
	
	public void setUsersPinned(@Nonnull final Set<UserConfiguration> usersPinned){
		this.usersPinned = usersPinned;
	}
}
