package fr.raksrinana.rsndiscord.utils.trakt.model.users.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.ISO8601DateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.themoviedb.model.Season;
import fr.raksrinana.rsndiscord.utils.themoviedb.model.TVDetails;
import fr.raksrinana.rsndiscord.utils.trakt.TraktObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.trakt.model.users.history.UserHistory.DATETIME_FORMAT;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Episode implements TraktObject{
	@JsonProperty("season")
	private int season;
	@JsonProperty("number")
	private int number;
	@JsonProperty("title")
	private String title;
	@JsonProperty("ids")
	private MediaIds ids;
	@JsonProperty("overview")
	private String overview;
	@JsonProperty("rating")
	private double rating;
	@JsonProperty("votes")
	private int votes;
	@JsonProperty("comment_count")
	private int commentCount;
	@JsonProperty("first_aired")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime firstAired;
	@JsonProperty("updated_at")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime updatedAt;
	@JsonProperty("runtime")
	private int runtime;
	
	@Override
	public void fillEmbed(@NonNull EmbedBuilder builder){
		fillEmbed(builder, null);
	}
	
	public void fillEmbed(@NonNull EmbedBuilder builder, TVDetails tvDetails){
		final var totalSeason = Optional.ofNullable(tvDetails).map(TVDetails::getNumberOfSeasons);
		final var episodesOfSeason = Optional.ofNullable(tvDetails).flatMap(details -> details.getSeason(getSeason())).map(Season::getEpisodeCount);
		builder.addField("Season", this.getSeason() + totalSeason.map(numberOfSeasons -> "/" + numberOfSeasons).orElse(""), true);
		builder.addField("Episode", this.getNumber() + episodesOfSeason.map(numberOfSeasons -> "/" + numberOfSeasons).orElse(""), true);
		builder.addField("Aired at", this.getFirstAired().format(DATETIME_FORMAT), true);
		Optional.ofNullable(this.getOverview()).ifPresent(overview -> builder.addField("Overview", overview, false));
	}
	
	@Override
	public URL getUrl(){
		return null;
	}
	
	@Override
	public int compareTo(@NonNull TraktObject o){
		if(o instanceof Episode){
			final var e = (Episode) o;
			if(getSeason() == e.getSeason()){
				return Integer.compare(getNumber(), e.getNumber());
			}
			return Integer.compare(getSeason(), e.getSeason());
		}
		return 0;
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(getSeason(), getNumber(), getIds());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		Episode episode = (Episode) o;
		return getSeason() == episode.getSeason() && getNumber() == episode.getNumber() && Objects.equals(getIds(), episode.getIds());
	}
}
