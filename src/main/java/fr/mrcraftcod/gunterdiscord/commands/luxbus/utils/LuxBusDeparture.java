package fr.mrcraftcod.gunterdiscord.commands.luxbus.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuxBusDeparture implements Comparable<LuxBusDeparture>{
	private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").optionalStart().appendPattern(" HH:mm:ss").optionalEnd().parseDefaulting(ChronoField.HOUR_OF_DAY, 0).parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0).parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0).toFormatter();
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
	private DateTimeFormatter dateTimeFormatterEmbedShort = new DateTimeFormatterBuilder().appendPattern("HH:mm").toFormatter();
	private DateTimeFormatter dateTimeFormatterEmbedLong = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm").toFormatter();
	
	@JsonCreator
	public static LuxBusDeparture createDeparture(@JsonProperty("date") final String date, @JsonProperty("time") final String time, @JsonProperty("rtDate") final String rtDate, @JsonProperty("rtTime") final String rtTime){
		final var departure = new LuxBusDeparture();
		departure.plannedDateTime = LocalDateTime.parse(String.format("%s %s", date, time), dateTimeFormatter);
		departure.realTimeDateTime = LocalDateTime.parse(String.format("%s %s", rtDate, rtTime), dateTimeFormatter);
		return departure;
	}
	
	@Override
	public int compareTo(@NotNull final LuxBusDeparture o){
		if(!Objects.equals(this.getProduct().getName(), o.getProduct().getName())){
			return this.getProduct().getName().compareTo(o.getProduct().getName());
		}
		if(!Objects.equals(this.getDirection(), o.getDirection())){
			return this.getDirection().compareTo(o.getDirection());
		}
		return this.getPlannedDateTime().compareTo(o.getPlannedDateTime());
	}
	
	public LuxBusProduct getProduct(){
		return this.product;
	}
	
	public String getDirection(){
		return this.direction;
	}
	
	public LocalDateTime getPlannedDateTime(){
		return this.plannedDateTime;
	}
	
	public EmbedBuilder getAsEmbed(final EmbedBuilder builder){
		final var delay = getPlannedDateTime().until(getRealTimeDateTime(), ChronoUnit.MINUTES);
		if(delay <= 2){
			builder.setColor(Color.GREEN);
		}
		else if(delay <= 4){
			builder.setColor(Color.ORANGE);
		}
		else{
			builder.setColor(Color.RED);
		}
		builder.setTitle(this.stop.getName());
		builder.setDescription(String.format("%s direction %s", this.getProduct().getName().replaceAll(" +", " "), this.getDirection()));
		builder.addField("Time", String.format("%s +%d", getEmbedDate(getPlannedDateTime()), delay), false);
		return builder;
	}
	
	public LocalDateTime getRealTimeDateTime(){
		return this.realTimeDateTime;
	}
	
	private String getEmbedDate(final LocalDateTime dateTime){
		if(LocalDate.now().equals(dateTime.toLocalDate())){
			return dateTime.format(this.dateTimeFormatterEmbedShort);
		}
		return dateTime.format(this.dateTimeFormatterEmbedLong);
	}
	
	public String getEntityNumber(){
		return this.entityNumber;
	}
	
	public LuxBusStop getStop(){
		return this.stop;
	}
}
