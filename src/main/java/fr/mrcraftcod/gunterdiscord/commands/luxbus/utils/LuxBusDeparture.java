package fr.mrcraftcod.gunterdiscord.commands.luxbus.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
	public static LuxBusDeparture createDeparture(@JsonProperty("date") String date, @JsonProperty("time") String time, @JsonProperty("rtDate") String rtDate, @JsonProperty("rtTime") String rtTime){
		final var departure = new LuxBusDeparture();
		departure.plannedDateTime = LocalDateTime.parse(String.format("%s %s", date, time), dateTimeFormatter);
		departure.realTimeDateTime = LocalDateTime.parse(String.format("%s %s", rtDate, rtTime), dateTimeFormatter);
		return departure;
	}
	
	@Override
	public int compareTo(@NotNull LuxBusDeparture o){
		if(!Objects.equals(this.getProduct().getName(), o.getProduct().getName())){
			return this.getProduct().getName().compareTo(o.getProduct().getName());
		}
		if(!Objects.equals(this.getDirection(), o.getDirection())){
			return this.getDirection().compareTo(o.getDirection());
		}
		return this.getPlannedDateTime().compareTo(o.getPlannedDateTime());
	}
	
	public LuxBusProduct getProduct(){
		return product;
	}
	
	public String getDirection(){
		return direction;
	}
	
	public LocalDateTime getPlannedDateTime(){
		return plannedDateTime;
	}
	
	public EmbedBuilder getAsEmbed(EmbedBuilder builder){
		final long delay = getPlannedDateTime().until(getRealTimeDateTime(), ChronoUnit.MINUTES);
		if(delay <= 2){
			builder.setColor(Color.GREEN);
		}
		else if(delay <= 4){
			builder.setColor(Color.ORANGE);
		}
		else{
			builder.setColor(Color.RED);
		}
		builder.setTitle(stop.getName());
		builder.setDescription(String.format("%s direction %s", this.getProduct().getName().replaceAll(" +", " "), this.getDirection()));
		builder.addField("Real at", getEmbedDate(getRealTimeDateTime()), false);
		builder.addField("Planned at", getEmbedDate(getPlannedDateTime()), false);
		return builder;
	}
	
	public LocalDateTime getRealTimeDateTime(){
		return realTimeDateTime;
	}
	
	private String getEmbedDate(LocalDateTime dateTime){
		if(LocalDate.now().equals(dateTime.toLocalDate())){
			return dateTime.format(dateTimeFormatterEmbedShort);
		}
		return dateTime.format(dateTimeFormatterEmbedLong);
	}
	
	public String getEntityNumber(){
		return entityNumber;
	}
	
	public LuxBusStop getStop(){
		return stop;
	}
}
