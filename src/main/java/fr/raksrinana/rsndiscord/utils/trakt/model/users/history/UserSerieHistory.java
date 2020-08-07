package fr.raksrinana.rsndiscord.utils.trakt.model.users.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.rsndiscord.utils.themoviedb.model.MediaDetails;
import fr.raksrinana.rsndiscord.utils.themoviedb.model.TVDetails;
import fr.raksrinana.rsndiscord.utils.trakt.TraktObject;
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
@JsonTypeName("episode")
public class UserSerieHistory extends UserHistory{
	@JsonProperty("episode")
	private Episode episode;
	@JsonProperty("show")
	private Show show;
	
	@Override
	public void fillEmbed(@NonNull Guild guild, @NonNull EmbedBuilder builder, MediaDetails mediaDetails){
		builder.setTitle(translate(guild, "trakt.watched.episode"), Optional.of(getUrl()).map(Object::toString).orElse(null));
		Optional.ofNullable(mediaDetails).flatMap(details -> details.getPosterURL(getEpisode().getSeason()).or(details::getPosterURL)).ifPresent(posterUrl -> builder.setThumbnail(posterUrl.toString()));
		this.getEpisode().fillEmbed(guild, builder, mediaDetails instanceof TVDetails ? (TVDetails) mediaDetails : null);
		builder.addBlankField(false);
		this.getShow().fillEmbed(guild, builder, mediaDetails instanceof TVDetails ? (TVDetails) mediaDetails : null);
		builder.addBlankField(false);
		super.fillEmbed(guild, builder, mediaDetails);
	}
	
	@Override
	public URL getUrl(){
		return this.getShow().getUrl();
	}
	
	@Override
	public int compareTo(@NonNull TraktObject o){
		if(o instanceof UserSerieHistory){
			final var h = (UserSerieHistory) o;
			if(Objects.equals(getShow(), h.getShow())){
				return getEpisode().compareTo(h.getEpisode());
			}
			return getShow().compareTo(h.getShow());
		}
		return super.compareTo(o);
	}
	
	@Override
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
