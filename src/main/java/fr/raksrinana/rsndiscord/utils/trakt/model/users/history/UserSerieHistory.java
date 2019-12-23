package fr.raksrinana.rsndiscord.utils.trakt.model.users.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;

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
		super.fillEmbed(builder);
		this.getShow().fillEmbed(builder);
		builder.addBlankField(false);
		this.getEpisode().fillEmbed(builder);
	}
}
