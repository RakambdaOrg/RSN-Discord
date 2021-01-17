package fr.raksrinana.rsndiscord.api.trakt.model.users.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.api.themoviedb.model.TVDetails;
import fr.raksrinana.rsndiscord.api.trakt.model.ITraktObject;
import fr.raksrinana.rsndiscord.utils.json.ISO8601ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class Show implements ITraktObject{
	@JsonProperty("title")
	private String title;
	@JsonProperty("year")
	private int year;
	@JsonProperty("ids")
	private MediaIds ids;
	@JsonProperty("overview")
	private String overview;
	@JsonProperty("first_aired")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime firstAired;
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
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime updatedAt;
	@JsonProperty("language")
	private String language;
	@JsonProperty("genres")
	private Set<String> genres;
	@JsonProperty("aired_episodes")
	private int airedEpisodes;
	
	@Override
	public void fillEmbed(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		fillEmbed(guild, builder, null);
	}
	
	public void fillEmbed(@NotNull Guild guild, @NotNull EmbedBuilder builder, @Nullable TVDetails tvDetails){
		builder.addField(translate(guild, "trakt.title"), getTitle(), true)
				.addField(translate(guild, "trakt.year"), Integer.toString(getYear()), true);
		ofNullable(tvDetails).map(TVDetails::getNumberOfSeasons)
				.ifPresent(numberOfSeasons -> builder.addField(translate(guild, "trakt.seasons"), Integer.toString(numberOfSeasons), true));
		builder.addField(translate(guild, "trakt.episodes"), Integer.toString(getAiredEpisodes()), true)
				.addField(translate(guild, "trakt.status"), getStatus(), true)
				.addField(translate(guild, "trakt.genres"), String.join(", ", getGenres()), true)
				.addField(translate(guild, "trakt.overview"), getOverview(), false);
	}
	
	@Override
	@Nullable
	public URL getUrl(){
		return getTrailer();
	}
	
	@Override
	public int compareTo(@NotNull ITraktObject o){
		if(o instanceof Show){
			return getTitle().compareTo(((Show) o).getTitle());
		}
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
