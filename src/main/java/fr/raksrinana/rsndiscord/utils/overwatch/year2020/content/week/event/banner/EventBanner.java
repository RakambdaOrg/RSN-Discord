package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event.banner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.HexColorDeserializer;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import fr.raksrinana.rsndiscord.utils.json.UnknownDeserializer;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.utils.team.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.awt.Color;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class EventBanner{
	@JsonProperty("title")
	private String title;
	@JsonProperty("backgroundImageUrl")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL backgroundImageUrl;
	@JsonProperty("backgroundVideos")
	@JsonDeserialize(contentUsing = UnknownDeserializer.class)
	private Set<Object> backgroundVideos;
	@JsonProperty("featuredImage")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL featuredImage;
	@JsonProperty("hostedBy")
	private String hostedBy;
	@JsonProperty("hostingTeam")
	private Team hostingTeam;
	@JsonProperty("headings")
	private Map<String, Heading> headings;
	@JsonProperty("sponsors")
	@JsonDeserialize(contentUsing = UnknownDeserializer.class)
	private Set<Object> sponsors;
	@JsonProperty("ticket")
	private Ticket ticket;
	@JsonProperty("venue")
	private Venue venue;
	@JsonProperty("bottomBackgroundColor")
	@JsonDeserialize(using = HexColorDeserializer.class)
	private Color bottomBackgroundColor;
	
	@Override
	public int hashCode(){
		return Objects.hash(getTitle(), getHostingTeam());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof EventBanner)){
			return false;
		}
		EventBanner that = (EventBanner) o;
		return Objects.equals(getTitle(), that.getTitle()) && Objects.equals(getHostingTeam(), that.getHostingTeam());
	}
}
