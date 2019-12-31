package fr.raksrinana.rsndiscord.utils.trakt.model.users.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import java.net.URL;
import java.util.Objects;

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
	public void fillEmbed(EmbedBuilder builder){
		this.getShow().fillEmbed(builder);
		builder.addBlankField(false);
		this.getEpisode().fillEmbed(builder);
		builder.addBlankField(false);
		super.fillEmbed(builder);
	}
	
	@Override
	public URL getUrl(){
		return this.getShow().getUrl();
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
