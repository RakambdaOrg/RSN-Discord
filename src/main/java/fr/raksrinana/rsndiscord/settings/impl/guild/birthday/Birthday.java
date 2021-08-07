package fr.raksrinana.rsndiscord.settings.impl.guild.birthday;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.utils.json.converter.ISO8601LocalDateDeserializer;
import fr.raksrinana.rsndiscord.utils.json.converter.ISO8601LocalDateSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
	private final Set<LocalDate> datesNotified = new HashSet<>();
	
	public Birthday(@NotNull LocalDate date){
		this.date = date;
	}
	
	public boolean isNotified(@NotNull LocalDate date){
		return datesNotified.contains(date);
	}
	
	public boolean isAt(@NotNull LocalDate date){
		return this.date.getMonth() == date.getMonth() && this.date.getDayOfMonth() == date.getDayOfMonth();
	}
	
	public void setNotified(@NotNull LocalDate date){
		datesNotified.add(date);
	}
}
