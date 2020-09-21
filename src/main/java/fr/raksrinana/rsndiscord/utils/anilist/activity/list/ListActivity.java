package fr.raksrinana.rsndiscord.utils.anilist.activity.list;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.anilist.AniListObject;
import fr.raksrinana.rsndiscord.utils.anilist.AnilistDatedObject;
import fr.raksrinana.rsndiscord.utils.anilist.media.Media;
import fr.raksrinana.rsndiscord.utils.json.SQLTimestampDeserializer;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.awt.Color;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = AnimeListActivity.class, name = "ANIME_LIST"),
		@JsonSubTypes.Type(value = MangaListActivity.class, name = "MANGA_LIST")
})
@Getter
public abstract class ListActivity implements AnilistDatedObject{
	@Getter
	private static final String QUERY = """
			ListActivity {
			    id
			    userId
			    type
			    createdAt
			    progress
			    siteUrl
			    %s
			}""".formatted(Media.getQUERY());
	@JsonProperty("createdAt")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private ZonedDateTime createdAt;
	@JsonProperty("siteUrl")
	private URL url;
	@JsonProperty("progress")
	private String progress;
	@JsonProperty("media")
	private Media media;
	@JsonProperty("id")
	private int id;
	
	@Override
	public void fillEmbed(@NonNull Guild guild, @NonNull final EmbedBuilder builder){
		builder.setColor(getColor());
		builder.setTimestamp(getDate());
		if(Objects.isNull(getProgress())){
			builder.setDescription(translate(guild, "anilist.list-added"));
		}
		else{
			builder.setDescription(StringUtils.capitalize(getMedia().getProgressType(getProgress().contains("-"))) + " " + getProgress());
		}
		builder.addBlankField(false);
		builder.addField(translate(guild, "anilist.media"), "", false);
		getMedia().fillEmbed(guild, builder);
	}
	
	@NonNull
	protected abstract Color getColor();
	
	@NonNull
	public ZonedDateTime getDate(){
		return this.getCreatedAt();
	}
	
	@Override
	public int compareTo(@NonNull final AniListObject o){
		if(o instanceof AnilistDatedObject){
			return getDate().compareTo(((AnilistDatedObject) o).getDate());
		}
		return Integer.compare(getId(), o.getId());
	}
	
	@Override
	public int hashCode(){
		return this.getId();
	}
	
	@Override
	public boolean equals(final Object obj){
		return obj instanceof ListActivity && Objects.equals(((ListActivity) obj).getId(), getId());
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
