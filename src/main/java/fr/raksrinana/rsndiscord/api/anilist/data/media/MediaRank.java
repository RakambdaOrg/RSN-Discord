package fr.raksrinana.rsndiscord.api.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.api.anilist.data.IAniListObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import static fr.raksrinana.rsndiscord.api.anilist.AniListApi.FALLBACK_URL;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class MediaRank implements IAniListObject{
	public static final String QUERY = """
			media {
			    id
			    rank
			    type
			    format
			    year
			    season
			    allTime
			    context
			}""";
	
	@JsonProperty("id")
	private int id;
	@JsonProperty("rank")
	private int rank;
	@JsonProperty("type")
	private MediaRankType type;
	@JsonProperty("format")
	private MediaFormat format;
	@JsonProperty("year")
	private Integer year;
	@JsonProperty("season")
	private MediaSeason season;
	@JsonProperty("allTime")
	private Boolean allTime;
	@JsonProperty("context")
	private String context;
	
	@Override
	public void fillEmbed(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		builder.addField("Ranking", getContext(), true);
	}
	
	@Override
	public int compareTo(@NotNull IAniListObject o){
		return Integer.compare(getId(), o.getId());
	}
	
	@Override
	@NotNull
	public URL getUrl(){
		return FALLBACK_URL;
	}
}
