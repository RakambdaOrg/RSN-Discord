package fr.raksrinana.rsndiscord.utils.uselessfacts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class UselessFact{
	@JsonProperty("id")
	private String id;
	@JsonProperty("text")
	private String text;
	@JsonProperty("source")
	private String source;
	@JsonProperty("source_url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL sourceUrl;
	@JsonProperty("language")
	private String language;
	@JsonProperty("permalink")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL permalink;
	
	public void fillEmbed(@NonNull EmbedBuilder builder){
		builder.setFooter(getId());
		builder.addField("Fact", getText(), true);
	}
}
