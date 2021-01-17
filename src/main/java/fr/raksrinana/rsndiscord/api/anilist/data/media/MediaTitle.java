package fr.raksrinana.rsndiscord.api.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class MediaTitle{
	public static final String QUERY = """
			title {
				userPreferred
				romaji
				english
				native
			}""";
	
	@JsonProperty("userPreferred")
	private String userPreferred;
	@JsonProperty("romaji")
	private String romaji;
	@JsonProperty("english")
	private String english;
	@JsonProperty("native")
	private String nativeTitle;
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
