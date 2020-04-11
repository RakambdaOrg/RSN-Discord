package fr.raksrinana.rsndiscord.runners;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.utils.http.requestssenders.get.ObjectGetRequestSender;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Covid19ScheduledRunner implements ScheduledRunner{
	private final JDA jda;
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Getter
	@NoArgsConstructor
	private static class Covid19CountryResponse{
		@JsonProperty("country")
		private String country;
		@JsonProperty("cases")
		private int cases;
		@JsonProperty("todayCases")
		private int todayCases;
		@JsonProperty("deaths")
		private int deaths;
		@JsonProperty("todayDeaths")
		private int todayDeaths;
		@JsonProperty("recovered")
		private int recovered;
		@JsonProperty("active")
		private int active;
		@JsonProperty("critical")
		private int critical;
	}
	
	public Covid19ScheduledRunner(@NonNull JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		Collection<MessageEmbed> embeds = getEmbeds();
		this.jda.getGuilds().stream().map(Settings::get).flatMap(settings -> settings.getCovid19Channel().stream()).flatMap(channel -> channel.getChannel().stream()).forEach(channel -> embeds.forEach(embed -> Actions.sendMessage(channel, "", embed)));
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Covid 19";
	}
	
	@Override
	public long getPeriod(){
		return 6;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
	
	private Collection<MessageEmbed> getEmbeds(){
		var request = new ObjectGetRequestSender<List<Covid19CountryResponse>>(new GenericType<>(){}, Unirest.get("https://coronavirus-19-api.herokuapp.com/countries")).getRequestHandler();
		if(request.getResult().isSuccess()){
			return request.getRequestResult().stream().filter(countryResponse -> Objects.equals(countryResponse.getCountry(), "France") || Objects.equals(countryResponse.getCountry(), "Thailand")).map(this::buildEmbed).collect(Collectors.toList());
		}
		return List.of();
	}
	
	private MessageEmbed buildEmbed(Covid19CountryResponse countryResponse){
		final var builder = Utilities.buildEmbed(this.jda.getSelfUser(), Color.GREEN, countryResponse.getCountry(), "https://www.worldometers.info/coronavirus/country/" + countryResponse.getCountry());
		builder.addField("Today cases", Integer.toString(countryResponse.getTodayCases()), true);
		builder.addField("Today deaths", Integer.toString(countryResponse.getTodayDeaths()), true);
		builder.addField("Total cases", Integer.toString(countryResponse.getCases()), true);
		builder.addField("Total deaths", Integer.toString(countryResponse.getDeaths()), true);
		builder.addField("Active", Integer.toString(countryResponse.getActive()), true);
		builder.addField("Recovered", Integer.toString(countryResponse.getRecovered()), true);
		builder.addField("Critical", Integer.toString(countryResponse.getCritical()), true);
		return builder.build();
	}
}
