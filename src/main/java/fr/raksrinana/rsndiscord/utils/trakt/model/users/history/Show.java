package fr.raksrinana.rsndiscord.utils.trakt.model.users.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.ISO8601DateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import fr.raksrinana.rsndiscord.utils.trakt.TraktObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import static fr.raksrinana.rsndiscord.utils.trakt.model.users.history.UserHistory.DATETIME_FORMAT;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class Show implements TraktObject{
	@JsonProperty("title")
	private String title;
	@JsonProperty("year")
	private int year;
	@JsonProperty("ids")
	private MediaIds ids;
	@JsonProperty("overview")
	private String overview;
	@JsonProperty("first_aired")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime firstAired;
	@JsonProperty("airs")
	private Airing airs;
	@JsonProperty("runtime")
	private int runtime;
	@JsonProperty("country")
	private String country;
	@JsonProperty("trailer")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL trailer;
	@JsonProperty("homepage")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL homepage;
	@JsonProperty("status")
	private String status;
	@JsonProperty("rating")
	private double rating;
	@JsonProperty("votes")
	private int votes;
	@JsonProperty("comment_count")
	private int commentCount;
	@JsonProperty("network")
	private String network;
	@JsonProperty("updated_at")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime updatedAt;
	@JsonProperty("language")
	private String language;
	@JsonProperty("genres")
	private Set<String> genres;
	@JsonProperty("aired_episodes")
	private int airedEpisodes;
	
	@Override
	public void fillEmbed(EmbedBuilder builder){
		builder.addField("Title", this.getTitle(), true);
		builder.addField("Year", Integer.toString(this.getYear()), true);
		builder.addField("Episodes", Integer.toString(this.getAiredEpisodes()), true);
		builder.addField("Status", this.getStatus(), true);
		builder.addField("Aired at", this.getFirstAired().format(DATETIME_FORMAT), true);
		builder.addField("Genres", String.join(", ", this.getGenres()), true);
		builder.addField("Overview", this.getOverview(), false);
	}
	
	@Override
	public URL getUrl(){
		return this.getTrailer();
	}
	
	@Override
	public int compareTo(@NonNull TraktObject o){
		return 0;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(getIds());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		Show show = (Show) o;
		return Objects.equals(getIds(), show.getIds());
	}
}
