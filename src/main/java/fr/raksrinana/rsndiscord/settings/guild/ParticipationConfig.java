package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.guild.participation.EntityParticipation;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
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
@NoArgsConstructor
public class ParticipationConfig{
	@JsonIgnore
	private final Object emotesLock = new Object();
	@JsonIgnore
	private final Object usersLock = new Object();
	@JsonProperty("emotes")
	@Getter
	private Set<EntityParticipation> emotesParticipation = new HashSet<>();
	@JsonProperty("users")
	private Set<EntityParticipation> usersParticipation = new HashSet<>();
	@JsonProperty("usersPenned")
	@Getter
	@Setter
	private Set<UserConfiguration> usersPinned = new HashSet<>();
	@JsonProperty("reportChannel")
	@Setter
	private ChannelConfiguration reportChannel;
	
	public EntityParticipation getUsers(@NonNull final LocalDate date){
		return this.getUsers(date, true).orElseThrow();
	}
	
	public Optional<EntityParticipation> getUsers(@NonNull final LocalDate date, final boolean create){
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
	
	private void addUserParticipation(@NonNull final EntityParticipation entityParticipation){
		this.usersParticipation.add(entityParticipation);
	}
	
	@NonNull
	public EntityParticipation getEmotes(@NonNull final LocalDate date){
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
	
	private void addEmoteParticipation(@NonNull final EntityParticipation entityParticipation){
		this.emotesParticipation.add(entityParticipation);
	}
	
	public void removeUsers(@NonNull final LocalDate date){
		this.usersParticipation.removeIf(p -> Objects.equals(p.getDate(), date));
	}
	
	public void removeEmotes(@NonNull final LocalDate date){
		this.emotesParticipation.removeIf(p -> Objects.equals(p.getDate(), date));
	}
	
	public void removeEmotesBefore(@NonNull final LocalDate week){
		this.emotesParticipation.removeIf(p -> p.getDate().isBefore(week));
	}
	
	public void removeUsersBefore(final LocalDate date){
		this.usersParticipation.removeIf(p -> p.getDate().isBefore(date));
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getReportChannel(){
		return Optional.ofNullable(this.reportChannel);
	}
}
