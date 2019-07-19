package fr.mrcraftcod.gunterdiscord.settings.guild.participation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.mrcraftcod.gunterdiscord.utils.json.LocalDateTimeDeserializer;
import fr.mrcraftcod.gunterdiscord.utils.json.LocalDateTimeSerializer;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityParticipation{
	@JsonProperty("date")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime date;
	@JsonProperty("scores")
	private Map<Long, Long> scores = new HashMap<>();
	
	public EntityParticipation(LocalDate date){
		this.date = LocalDateTime.of(date, LocalTime.of(0, 0, 0));
	}
	
	public EntityParticipation(){
	}
	
	public void increment(long id){
		scores.compute(id, (key, val) -> Optional.ofNullable(val).orElse(0L) + 1);
	}
	
	@Nonnull
	public LocalDate getDate(){
		return this.date.toLocalDate();
	}
	
	@Nonnull
	public Map<Long, Long> getScores(){
		return this.scores;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(getDate()).toHashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof EntityParticipation)){
			return false;
		}
		EntityParticipation that = (EntityParticipation) o;
		return new EqualsBuilder().append(getDate(), that.getDate()).isEquals();
	}
}
