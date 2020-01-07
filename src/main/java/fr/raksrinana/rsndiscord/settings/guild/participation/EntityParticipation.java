package fr.raksrinana.rsndiscord.settings.guild.participation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.CompositeConfiguration;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class EntityParticipation implements CompositeConfiguration{
	@JsonProperty("date")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime dateTime;
	@JsonProperty("scores")
	private Set<EntityScore> scores = new HashSet<>();
	
	public EntityParticipation(final LocalDate date){
		this.dateTime = LocalDateTime.of(date, LocalTime.of(0, 0, 0));
	}
	
	public void increment(final long id, final String name){
		this.scores.stream().filter(e -> Objects.equals(e.getId(), id)).findFirst().ifPresentOrElse(EntityScore::increment, () -> this.scores.add(new EntityScore(id, name, 1)));
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(this.getDate()).toHashCode();
	}
	
	public LocalDate getDate(){
		return this.getDateTime().toLocalDate();
	}
	
	@Override
	public boolean equals(final Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof EntityParticipation)){
			return false;
		}
		final var that = (EntityParticipation) o;
		return new EqualsBuilder().append(this.getDate(), that.getDate()).isEquals();
	}
}
