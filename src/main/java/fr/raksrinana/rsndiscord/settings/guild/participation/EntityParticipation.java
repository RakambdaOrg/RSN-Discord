package fr.raksrinana.rsndiscord.settings.guild.participation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.CompositeConfiguration;
import fr.raksrinana.rsndiscord.utils.json.ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ZonedDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class EntityParticipation implements CompositeConfiguration{
	@JsonProperty("scores")
	private final Set<EntityScore> scores = new HashSet<>();
	@JsonProperty("date")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	private ZonedDateTime dateTime;
	
	public EntityParticipation(final LocalDate date){
		this.dateTime = ZonedDateTime.of(date, LocalTime.MIDNIGHT, ZoneId.of("UTC"));
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
