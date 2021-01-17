package fr.raksrinana.rsndiscord.api.trakt.model.users.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.rsndiscord.api.themoviedb.model.MediaDetails;
import fr.raksrinana.rsndiscord.api.themoviedb.model.MovieDetails;
import fr.raksrinana.rsndiscord.api.trakt.model.ITraktObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
@JsonTypeName("movie")
public class UserMovieHistory extends UserHistory{
	@JsonProperty("movie")
	private Movie movie;
	
	@Override
	public void fillEmbed(@NotNull Guild guild, @NotNull EmbedBuilder builder, @Nullable MediaDetails mediaDetails){
		builder.setTitle(translate(guild, "trakt.watched.movie"), ofNullable(getUrl()).map(Object::toString).orElse(null));
		ofNullable(mediaDetails).flatMap(MediaDetails::getPosterURL)
				.ifPresent(posterUrl -> builder.setThumbnail(posterUrl.toString()));
		
		getMovie().fillEmbed(guild, builder, mediaDetails instanceof MovieDetails ? (MovieDetails) mediaDetails : null);
		builder.addBlankField(false);
		
		super.fillEmbed(guild, builder, mediaDetails);
	}
	
	@Override
	@Nullable
	public URL getUrl(){
		return getMovie().getUrl();
	}
	
	@Override
	public int compareTo(@NotNull ITraktObject o){
		if(o instanceof UserMovieHistory){
			return getMovie().compareTo(((UserMovieHistory) o).getMovie());
		}
		return super.compareTo(o);
	}
	
	@Override
	@NotNull
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
