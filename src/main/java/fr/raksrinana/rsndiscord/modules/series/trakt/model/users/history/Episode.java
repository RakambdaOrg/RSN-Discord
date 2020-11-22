package fr.raksrinana.rsndiscord.modules.series.trakt.model.users.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.modules.series.themoviedb.model.Season;
import fr.raksrinana.rsndiscord.modules.series.themoviedb.model.TVDetails;
import fr.raksrinana.rsndiscord.modules.series.trakt.model.ITraktObject;
import fr.raksrinana.rsndiscord.utils.json.ISO8601ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.modules.series.trakt.model.users.history.UserHistory.DATETIME_FORMAT;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Episode implements ITraktObject{
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
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime firstAired;
	@JsonProperty("updated_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime updatedAt;
	@JsonProperty("runtime")
	private int runtime;
	
	@Override
	public void fillEmbed(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		fillEmbed(guild, builder, null);
	}
	
	public void fillEmbed(@NonNull Guild guild, @NonNull EmbedBuilder builder, TVDetails tvDetails){
		var totalSeason = ofNullable(tvDetails).map(TVDetails::getNumberOfSeasons);
		var episodesOfSeason = ofNullable(tvDetails).flatMap(details -> details.getSeason(getSeason())).map(Season::getEpisodeCount);
		
		var season = getSeason() + totalSeason.map(numberOfSeasons -> "/" + numberOfSeasons).orElse("");
		var episode = getNumber() + episodesOfSeason.map(numberOfSeasons -> "/" + numberOfSeasons).orElse("");
		
		builder.addField(translate(guild, "trakt.season"), season, true)
				.addField(translate(guild, "trakt.episode"), episode, true)
				.addField(translate(guild, "trakt.aired"), this.getFirstAired().format(DATETIME_FORMAT), true);
		ofNullable(this.getOverview()).ifPresent(overview -> builder.addField(translate(guild, "trakt.overview"), overview, false));
	}
	
	@Override
	public URL getUrl(){
		return null;
	}
	
	@Override
	public int compareTo(@NonNull ITraktObject o){
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
