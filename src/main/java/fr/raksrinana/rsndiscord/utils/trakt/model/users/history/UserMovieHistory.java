package fr.raksrinana.rsndiscord.utils.trakt.model.users.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.rsndiscord.utils.trakt.TraktObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
@JsonTypeName("movie")
public class UserMovieHistory extends UserHistory{
	@JsonProperty("movie")
	private Movie movie;
	
	@Override
	public void fillEmbed(EmbedBuilder builder){
		builder.setTitle("Movie watched", Optional.of(getUrl()).map(Object::toString).orElse(null));
		this.getMovie().fillEmbed(builder);
		builder.addBlankField(false);
		super.fillEmbed(builder);
	}
	
	@Override
	public URL getUrl(){
		return this.getMovie().getUrl();
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(getMovie());
	}
	
	@Override
	public int compareTo(@NonNull TraktObject o){
		if(o instanceof UserMovieHistory){
			return getMovie().compareTo(((UserMovieHistory) o).getMovie());
		}
		return super.compareTo(o);
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
