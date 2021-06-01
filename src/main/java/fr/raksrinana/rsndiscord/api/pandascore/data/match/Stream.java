package fr.raksrinana.rsndiscord.api.pandascore.data.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.converter.URLDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Stream implements Comparable<Stream>{
	@JsonProperty("embed_url")
	@JsonDeserialize(using = URLDeserializer.class)
	@Nullable
	private URL embedUrl;
	@JsonProperty("language")
	@NotNull
	private Locale language;
	@JsonProperty("main")
	private boolean main;
	@JsonProperty("official")
	private boolean official;
	@JsonProperty("raw_url")
	@JsonDeserialize(using = URLDeserializer.class)
	@Nullable
	private URL url;
	
	@Override
	public int compareTo(@NotNull Stream o){
		if(isMain()){
			return -1;
		}
		if(isOfficial() && !o.isOfficial()){
			return -1;
		}
		if(!isOfficial() && o.isOfficial()){
			return 1;
		}
		return language.equals(Locale.ENGLISH) ? -1 : 0;
	}
	
	@NotNull
	public Optional<String> getUrlAsString(){
		return Optional.ofNullable(getUrl()).map(URL::toString);
	}
}
