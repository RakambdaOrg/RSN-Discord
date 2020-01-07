package fr.raksrinana.rsndiscord.utils.luxbus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class LuxBusDeparture implements Comparable<LuxBusDeparture>{
	private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").optionalStart().appendPattern(" HH:mm:ss").optionalEnd().parseDefaulting(ChronoField.HOUR_OF_DAY, 0).parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0).parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0).toFormatter();
	private static final Pattern SPACE_PATTERN = Pattern.compile(" +");
	private final DateTimeFormatter dateTimeFormatterEmbedShort = new DateTimeFormatterBuilder().appendPattern("HH:mm").toFormatter();
	private final DateTimeFormatter dateTimeFormatterEmbedLong = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm").toFormatter();
	@JsonProperty("stopid")
	private LuxBusStop stop;
	@JsonProperty("trainNumber")
	private String entityNumber;
	private LocalDateTime plannedDateTime;
	private LocalDateTime realTimeDateTime;
	@JsonProperty("Product")
	private LuxBusProduct product;
	@JsonProperty("direction")
	private String direction;
	
	@JsonCreator
	@NonNull
	public static LuxBusDeparture createDeparture(@NonNull @JsonProperty("date") final String date, @NonNull @JsonProperty("time") final String time, @JsonProperty("rtDate") final String realTimeDate, @JsonProperty("rtTime") final String realTimeTime){
		final var departure = new LuxBusDeparture();
		departure.plannedDateTime = LocalDateTime.parse(String.format("%s %s", date, time), dateTimeFormatter);
		departure.realTimeDateTime = Objects.nonNull(realTimeDate) && Objects.nonNull(realTimeTime) ? LocalDateTime.parse(String.format("%s %s", realTimeDate, realTimeTime), dateTimeFormatter) : null;
		return departure;
	}
	
	@Override
	public int compareTo(@NonNull final LuxBusDeparture departure){
		if(!Objects.equals(this.getProduct().getName(), departure.getProduct().getName())){
			return this.getProduct().getName().compareTo(departure.getProduct().getName());
		}
		if(!Objects.equals(this.getDirection(), departure.getDirection())){
			return this.getDirection().compareTo(departure.getDirection());
		}
		return this.getPlannedDateTime().compareTo(departure.getPlannedDateTime());
	}
	
	@NonNull
	public EmbedBuilder getAsEmbed(@NonNull final EmbedBuilder embedBuilder){
		final var delayMinutes = this.getRealTimeDateTime().map(realDateTime -> this.getPlannedDateTime().until(realDateTime, ChronoUnit.MINUTES)).orElse(null);
		var pattern = "%s";
		if(Objects.nonNull(delayMinutes)){
			pattern = "%s +%d";
			if(delayMinutes <= 2){
				embedBuilder.setColor(Color.GREEN);
			}
			else if(delayMinutes <= 4){
				embedBuilder.setColor(Color.ORANGE);
			}
			else{
				embedBuilder.setColor(Color.RED);
			}
		}
		embedBuilder.setTitle(this.stop.getName());
		embedBuilder.setDescription(String.format("%s direction %s", SPACE_PATTERN.matcher(this.getProduct().getName()).replaceAll(" "), this.getDirection()));
		embedBuilder.addField("Time", String.format(pattern, this.getEmbedDate(this.getPlannedDateTime()), delayMinutes), false);
		return embedBuilder;
	}
	
	@NonNull
	private Optional<LocalDateTime> getRealTimeDateTime(){
		return Optional.ofNullable(this.realTimeDateTime);
	}
	
	@NonNull
	private String getEmbedDate(@NonNull final LocalDateTime dateTime){
		if(LocalDate.now().equals(dateTime.toLocalDate())){
			return dateTime.format(this.dateTimeFormatterEmbedShort);
		}
		return dateTime.format(this.dateTimeFormatterEmbedLong);
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(stop, entityNumber, plannedDateTime, direction);
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		LuxBusDeparture that = (LuxBusDeparture) o;
		return Objects.equals(stop, that.stop) && Objects.equals(entityNumber, that.entityNumber) && Objects.equals(plannedDateTime, that.plannedDateTime) && Objects.equals(direction, that.direction);
	}
}
