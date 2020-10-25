package fr.raksrinana.rsndiscord.modules.anilist.data.airing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.modules.anilist.data.IAniListObject;
import fr.raksrinana.rsndiscord.modules.anilist.data.IAnilistDatedObject;
import fr.raksrinana.rsndiscord.modules.anilist.data.media.IMedia;
import fr.raksrinana.rsndiscord.utils.json.SQLTimestampDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class AiringSchedule implements IAnilistDatedObject{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
	@Getter
	private static final String QUERY = """
			airingSchedules(mediaId: $mediaID) {
			    id
			    airingAt
			    episode
			    timeUntilAiring
			    %s
			}""".formatted(IMedia.getQUERY());
	@JsonProperty("id")
	private int id;
	@JsonProperty("airingAt")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private ZonedDateTime airingAt;
	@JsonProperty("episode")
	private int episode;
	@JsonProperty("media")
	private IMedia media;
	@JsonProperty("timeUntilAiring")
	private int timeUntilAiring;
	
	@Override
	public void fillEmbed(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		builder.addField(translate(guild, "anilist.episode"), Integer.toString(getEpisode()), true);
		builder.addField(translate(guild, "anilist.air"), getAiringAt().format(DF), true);
		builder.addBlankField(false);
		getMedia().fillEmbed(guild, builder);
	}
	
	@Override
	public @NonNull URL getUrl(){
		return getMedia().getUrl();
	}
	
	@Override
	public int compareTo(@NonNull IAniListObject o){
		if(o instanceof IAnilistDatedObject){
			return getDate().compareTo(((IAnilistDatedObject) o).getDate());
		}
		return Integer.compare(getId(), o.getId());
	}
	
	@Override
	public @NonNull ZonedDateTime getDate(){
		return getAiringAt();
	}
}
