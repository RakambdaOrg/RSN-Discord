package fr.raksrinana.rsndiscord.utils.anilist.airing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.anilist.AniListObject;
import fr.raksrinana.rsndiscord.utils.anilist.AnilistDatedObject;
import fr.raksrinana.rsndiscord.utils.anilist.media.Media;
import fr.raksrinana.rsndiscord.utils.json.SQLTimestampDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class AiringSchedule implements AnilistDatedObject{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
	@Getter
	private static final String QUERY = "airingSchedules(mediaId: $mediaID) {\n" + "id\n" + "airingAt\n" + "episode\n" + "timeUntilAiring\n" + Media.getQUERY() + "\n}";
	@JsonProperty("id")
	private int id;
	@JsonProperty("airingAt")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private ZonedDateTime airingAt;
	@JsonProperty("episode")
	private int episode;
	@JsonProperty("media")
	private Media media;
	@JsonProperty("timeUntilAiring")
	private int timeUntilAiring;
	
	@Override
	public void fillEmbed(@NonNull EmbedBuilder builder){
		builder.addField("episode", Integer.toString(getEpisode()), true);
		builder.addField("Airs at", getAiringAt().format(DF), true);
		builder.addBlankField(false);
		getMedia().fillEmbed(builder);
	}
	
	@Override
	public @NonNull URL getUrl(){
		return getMedia().getUrl();
	}
	
	@Override
	public int compareTo(@NonNull AniListObject o){
		if(o instanceof AnilistDatedObject){
			return getDate().compareTo(((AnilistDatedObject) o).getDate());
		}
		return Integer.compare(getId(), o.getId());
	}
	
	@Override
	public @NonNull ZonedDateTime getDate(){
		return getAiringAt();
	}
}
