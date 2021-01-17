package fr.raksrinana.rsndiscord.api.anilist.data.list;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.api.anilist.data.IAniListDatedObject;
import fr.raksrinana.rsndiscord.api.anilist.data.IAniListObject;
import fr.raksrinana.rsndiscord.api.anilist.data.media.IMedia;
import fr.raksrinana.rsndiscord.utils.json.SQLTimestampDeserializer;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Objects.isNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = AnimeListActivity.class, name = "ANIME_LIST"),
		@JsonSubTypes.Type(value = MangaListActivity.class, name = "MANGA_LIST")
})
@Getter
public abstract class ListActivity implements IAniListDatedObject{
	public static final String QUERY = """
			ListActivity {
			    id
			    userId
			    type
			    createdAt
			    progress
			    siteUrl
			    %s
			}""".formatted(IMedia.QUERY);
	
	@JsonProperty("createdAt")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private ZonedDateTime createdAt;
	@JsonProperty("siteUrl")
	private URL url;
	@JsonProperty("progress")
	private String progress;
	@JsonProperty("media")
	private IMedia media;
	@JsonProperty("id")
	private int id;
	
	@Override
	public void fillEmbed(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		builder.setColor(getColor())
				.setTimestamp(getDate());
		if(isNull(getProgress())){
			builder.setDescription(translate(guild, "anilist.list-added"));
		}
		else{
			builder.setDescription(StringUtils.capitalize(getMedia().getProgressType(getProgress().contains("-"))) + " " + getProgress());
		}
		builder.addBlankField(false)
				.addField(translate(guild, "anilist.media"), "", false);
		getMedia().fillEmbed(guild, builder);
	}
	
	@NotNull
	protected abstract Color getColor();
	
	@NotNull
	public ZonedDateTime getDate(){
		return getCreatedAt();
	}
	
	@Override
	public int compareTo(@NotNull IAniListObject o){
		if(o instanceof IAniListDatedObject){
			return getDate().compareTo(((IAniListDatedObject) o).getDate());
		}
		return Integer.compare(getId(), o.getId());
	}
	
	@Override
	public int hashCode(){
		return getId();
	}
	
	@Override
	public boolean equals(Object obj){
		return obj instanceof ListActivity && Objects.equals(((ListActivity) obj).getId(), getId());
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
