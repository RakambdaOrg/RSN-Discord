package fr.raksrinana.rsndiscord.utils.giphy.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Gif{
	@JsonProperty("type")
	private String type;
	@JsonProperty("id")
	private String id;
	@JsonProperty("slug")
	private String slug;
	@JsonProperty("url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL url;
	@JsonProperty("bitly_url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL bitlyUrl;
	@JsonProperty("embed_url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL embedUrl;
	@JsonProperty("username")
	private String username;
	@JsonProperty("source")
	private String source;
	@JsonProperty("rating")
	private Rating rating;
	@JsonProperty("user")
	private User user;
	@JsonProperty("source_tld")
	private String sourceTld;
	@JsonProperty("source_post_url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL sourcePostUrl;
	@JsonProperty("update_datetime")
	private String updateDatetime;
	@JsonProperty("import_datetime")
	private String importDatetime;
	@JsonProperty("trending_datetime")
	private String trendingDatetime;
	@JsonProperty("title")
	private String title;
	@JsonProperty("images")
	private Images images;
}
