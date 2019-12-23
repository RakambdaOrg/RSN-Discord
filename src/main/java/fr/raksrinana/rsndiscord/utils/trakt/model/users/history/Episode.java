package fr.raksrinana.rsndiscord.utils.trakt.model.users.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.trakt.TraktObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;

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
	
	@Override
	public void fillEmbed(EmbedBuilder builder){
		builder.addField("Season", Integer.toString(this.getSeason()), true);
		builder.addField("Episode", Integer.toString(this.getNumber()), true);
	}
	
	@Override
	public int compareTo(@NonNull TraktObject o){
		return 0;
	}
}
