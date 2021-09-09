package fr.raksrinana.rsndiscord.settings.impl.guild.rss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class FeedInfo{
	@JsonProperty("title")
	private String title;
	@JsonProperty("lastPublicationDate")
	private Long lastPublicationDate;
	
	@NotNull
	public Optional<Long> getLastPublicationDate(){
		return Optional.ofNullable(lastPublicationDate);
	}
	
	public void setLastPublicationDate(long lastPublicationDate){
		this.lastPublicationDate = lastPublicationDate;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
}
