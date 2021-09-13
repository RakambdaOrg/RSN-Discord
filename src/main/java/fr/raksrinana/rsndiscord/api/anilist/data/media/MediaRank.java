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
import java.util.Objects;
import static fr.raksrinana.rsndiscord.api.anilist.AniListApi.FALLBACK_URL;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class MediaRank implements IAniListObject{
	public static final String QUERY = """
			rankings {
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
	private boolean allTime;
	@JsonProperty("context")
	private String context;
	
	@Override
	public void fillEmbed(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		var sb = new StringBuilder(getType().getIcon())
				.append(" ")
				.append("#")
				.append(getRank())
				.append(" ")
				.append(getContext());
		
		if(Objects.nonNull(getSeason())){
			sb.append(" ").append(getSeason());
		}
		
		if(Objects.nonNull(getYear())){
			sb.append(" ").append(getYear());
		}
		
		builder.addField(translate(guild, "anilist.ranking"), sb.toString(), true);
	}
	
	@Override
	public int compareTo(@NotNull IAniListObject o){
		if(o instanceof MediaRank mr){
			if(isAllTime() && mr.isAllTime()){
				return 0;
			}
			if(isAllTime()){
				return -1;
			}
			if(mr.isAllTime()){
				return 1;
			}
			
			var y1 = getYear() == null ? 0 : getYear();
			var y2 = mr.getYear() == null ? 0 : mr.getYear();
			if(y1 != y2){
				return Integer.compare(y1, y2);
			}
			
			var s1 = getSeason() == null ? 0 : getSeason().getIndex();
			var s2 = getSeason() == null ? 0 : getSeason().getIndex();
			if(s1 != s2){
				return Integer.compare(s1, s2);
			}
		}
		return Integer.compare(getId(), o.getId());
	}
	
	@Override
	@NotNull
	public URL getUrl(){
		return FALLBACK_URL;
	}
}
