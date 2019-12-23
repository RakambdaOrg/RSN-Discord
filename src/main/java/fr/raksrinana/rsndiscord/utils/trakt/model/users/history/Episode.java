package fr.raksrinana.rsndiscord.utils.trakt.model.users.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.ISO8601DateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.trakt.TraktObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import java.net.URL;
import java.time.LocalDateTime;

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
	@JsonProperty("overview")
	private String overview;
	@JsonProperty("rating")
	private double rating;
	@JsonProperty("votes")
	private int votes;
	@JsonProperty("comment_count")
	private int commentCount;
	@JsonProperty("first_aired")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime firstAired;
	@JsonProperty("updated_at")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime updatedAt;
	@JsonProperty("runtime")
	private int runtime;
	
	@Override
	public void fillEmbed(EmbedBuilder builder){
		builder.addField("Season", Integer.toString(this.getSeason()), true);
		builder.addField("Episode", Integer.toString(this.getNumber()), true);
	}
	
	@Override
	public URL getUrl(){
		return null;
	}
	
	@Override
	public int compareTo(@NonNull TraktObject o){
		return 0;
	}
}
