package fr.raksrinana.rsndiscord.api.trakt.model.users.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.rsndiscord.api.themoviedb.model.MediaDetails;
import fr.raksrinana.rsndiscord.api.themoviedb.model.TVDetails;
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
@JsonTypeName("episode")
public class UserSerieHistory extends UserHistory{
	@JsonProperty("episode")
	private Episode episode;
	@JsonProperty("show")
	private Show show;
	
	@Override
	public void fillEmbed(@NotNull Guild guild, @NotNull EmbedBuilder builder, @Nullable MediaDetails mediaDetails){
		builder.setTitle(translate(guild, "trakt.watched.episode"), ofNullable(getUrl()).map(Object::toString).orElse(null));
		ofNullable(mediaDetails).flatMap(details -> details.getPosterURL(getEpisode().getSeason())
				.or(details::getPosterURL))
				.ifPresent(posterUrl -> builder.setThumbnail(posterUrl.toString()));
		
		getEpisode().fillEmbed(guild, builder, mediaDetails instanceof TVDetails ? (TVDetails) mediaDetails : null);
		builder.addBlankField(false);
		
		getShow().fillEmbed(guild, builder, mediaDetails instanceof TVDetails ? (TVDetails) mediaDetails : null);
		builder.addBlankField(false);
		
		super.fillEmbed(guild, builder, mediaDetails);
	}
	
	@Override
	@Nullable
	public URL getUrl(){
		return getShow().getUrl();
	}
	
	@Override
	public int compareTo(@NotNull ITraktObject o){
		if(o instanceof UserSerieHistory h){
			if(Objects.equals(getShow(), h.getShow())){
				return getEpisode().compareTo(h.getEpisode());
			}
			return getShow().compareTo(h.getShow());
		}
		return super.compareTo(o);
	}
	
	@Override
	@NotNull
	public MediaIds getIds(){
		return getShow().getIds();
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(getEpisode(), getShow());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		UserSerieHistory that = (UserSerieHistory) o;
		return Objects.equals(getEpisode(), that.getEpisode()) && Objects.equals(getShow(), that.getShow());
	}
}
