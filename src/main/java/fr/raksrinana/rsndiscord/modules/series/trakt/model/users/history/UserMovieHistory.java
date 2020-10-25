package fr.raksrinana.rsndiscord.modules.series.trakt.model.users.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.rsndiscord.modules.series.themoviedb.model.MediaDetails;
import fr.raksrinana.rsndiscord.modules.series.themoviedb.model.MovieDetails;
import fr.raksrinana.rsndiscord.modules.series.trakt.model.ITraktObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
@JsonTypeName("movie")
public class UserMovieHistory extends UserHistory{
	@JsonProperty("movie")
	private Movie movie;
	
	@Override
	public void fillEmbed(@NonNull Guild guild, @NonNull EmbedBuilder builder, MediaDetails mediaDetails){
		builder.setTitle(translate(guild, "trakt.watched.movie"), Optional.of(getUrl()).map(Object::toString).orElse(null));
		Optional.ofNullable(mediaDetails).flatMap(MediaDetails::getPosterURL).ifPresent(posterUrl -> builder.setThumbnail(posterUrl.toString()));
		this.getMovie().fillEmbed(guild, builder, mediaDetails instanceof MovieDetails ? (MovieDetails) mediaDetails : null);
		builder.addBlankField(false);
		super.fillEmbed(guild, builder, mediaDetails);
	}
	
	@Override
	public URL getUrl(){
		return this.getMovie().getUrl();
	}
	
	@Override
	public int compareTo(@NonNull ITraktObject o){
		if(o instanceof UserMovieHistory){
			return getMovie().compareTo(((UserMovieHistory) o).getMovie());
		}
		return super.compareTo(o);
	}
	
	@Override
	public MediaIds getIds(){
		return getMovie().getIds();
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(getMovie());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		UserMovieHistory that = (UserMovieHistory) o;
		return Objects.equals(getMovie(), that.getMovie());
	}
}
