package fr.raksrinana.rsndiscord.api.uselessfacts.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.converter.URLDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
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
	
	public void fillEmbed(@NotNull EmbedBuilder builder){
		builder.setFooter(getId())
				.addField("Fact", getText(), true);
	}
	
	@Override
	public String toString(){
		return "id: " + getId() + " / " + getText();
	}
}
