package fr.raksrinana.rsndiscord.api.anilist.data.airing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.api.anilist.data.IAniListDatedObject;
import fr.raksrinana.rsndiscord.api.anilist.data.IAniListObject;
import fr.raksrinana.rsndiscord.api.anilist.data.media.IMedia;
import fr.raksrinana.rsndiscord.utils.json.converter.SQLTimestampDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class AiringSchedule implements IAniListDatedObject{
	public static final String QUERY = """
			airingSchedules(mediaId: $mediaID) {
			    id
			    airingAt
			    episode
			    timeUntilAiring
			    %s
			}""".formatted(IMedia.QUERY);
	
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
	
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
	public void fillEmbed(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		builder.addField(translate(guild, "anilist.episode"), Integer.toString(getEpisode()), true)
				.addField(translate(guild, "anilist.air"), getAiringAt().format(DF), true)
				.addBlankField(false);
		getMedia().fillEmbed(guild, builder);
	}
	
	@NotNull
	@Override
	public URL getUrl(){
		return getMedia().getUrl();
	}
	
	@Override
	public int compareTo(@NotNull IAniListObject o){
		if(o instanceof IAniListDatedObject){
			return getDate().compareTo(((IAniListDatedObject) o).getDate());
		}
		return Integer.compare(getId(), o.getId());
	}
	
	@NotNull
	@Override
	public ZonedDateTime getDate(){
		return getAiringAt();
	}
}
