package fr.mrcraftcod.gunterdiscord.settings.guild.participation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.mrcraftcod.gunterdiscord.utils.json.SQLTimestampDeserializer;
import fr.mrcraftcod.gunterdiscord.utils.json.SQLTimestampSerializer;
import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityParticipation{
	@JsonProperty("date")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	@JsonSerialize(using = SQLTimestampSerializer.class)
	private Date date;
	@JsonProperty("scores")
	private Map<Long, Long> scores = new HashMap<>();
	
	public EntityParticipation(LocalDate date){
		this.date = new Date(date.toEpochSecond(LocalTime.now(), ZoneOffset.of("UTC")));
	}
	
	public EntityParticipation(){
	}
	
	public void increment(long id){
		scores.compute(id, (key, val) -> Optional.ofNullable(val).orElse(0L) + 1);
	}
	
	@Nonnull
	public LocalDate getDate(){
		return LocalDate.ofInstant(this.date.toInstant(), ZoneId.of("UTC"));
	}
	
	@Nonnull
	public Map<Long, Long> getScores(){
		return this.scores;
	}
}
