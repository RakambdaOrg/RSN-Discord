package fr.raksrinana.rsndiscord.settings.guild.birthday;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Birthday{
	@JsonProperty("date")
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@JsonSerialize(using = ISO8601LocalDateSerializer.class)
	private LocalDate date;
	@JsonProperty("datesNotified")
	@JsonDeserialize(contentUsing = ISO8601LocalDateDeserializer.class)
	@JsonSerialize(contentUsing = ISO8601LocalDateSerializer.class)
	private Set<LocalDate> datesNotified = new HashSet<>();
	
	public Birthday(LocalDate date){
		this.date = date;
	}
	
	public boolean isNotified(LocalDate date){
		return datesNotified.contains(date);
	}
	
	public boolean isAt(LocalDate date){
		return this.date.getMonth() == date.getMonth() && this.date.getDayOfMonth() == date.getDayOfMonth();
	}
	
	public void setNotified(LocalDate date){
		datesNotified.add(date);
	}
}
