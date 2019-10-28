package fr.raksrinana.rsndiscord.utils.anilist.activity.list;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.anilist.AniListObject;
import fr.raksrinana.rsndiscord.utils.anilist.DatedObject;
import fr.raksrinana.rsndiscord.utils.anilist.media.AnimeMedia;
import fr.raksrinana.rsndiscord.utils.anilist.media.MangaMedia;
import fr.raksrinana.rsndiscord.utils.anilist.media.Media;
import fr.raksrinana.rsndiscord.utils.json.SQLTimestampDeserializer;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
@SuppressWarnings("ALL")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = AnimeMedia.class, name = "ANIME_LIST"),
		@JsonSubTypes.Type(value = MangaMedia.class, name = "MANGA_LIST")
})
public abstract class ListActivity implements DatedObject{
	private static final String QUERY = "ListActivity{\n" + "id\n" + "userId\n" + "type\n" + "createdAt\n" + "progress\n" + "siteUrl\n" + Media.getQuery() + "\n}";
	@JsonProperty("createdAt")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private LocalDateTime createdAt;
	@JsonProperty("siteUrl")
	private URL url;
	@JsonProperty("progress")
	private String progress;
	@JsonProperty("media")
	private Media media;
	@JsonProperty("id")
	private int id;
	
	@Override
	public boolean equals(@Nullable final Object obj){
		return obj instanceof ListActivity && Objects.equals(((ListActivity) obj).getId(), getId());
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	public void fillEmbed(@Nonnull final EmbedBuilder builder){
		builder.setColor(getColor());
		builder.setTimestamp(getDate());
		
		if(Objects.isNull(getProgress())){
			builder.setDescription("Added to list");
		}
		else{
			builder.setDescription(StringUtils.capitalize(getMedia().getProgressType(getProgress().contains("-"))) + " " + getProgress());
		}
		
		builder.addBlankField(false);
		builder.addField("Media:", "", false);
		getMedia().fillEmbed(builder);
	}
	
	@Override
	public int getId(){
		return this.id;
	}
	
	@Nonnull
	protected abstract Color getColor();
	
	@Nonnull
	public LocalDateTime getDate(){
		return createdAt;
	}
	
	@Nonnull
	public String getProgress(){
		return progress;
	}
	
	@Override
	public int compareTo(@Nonnull final AniListObject o){
		if(o instanceof DatedObject){
			return getDate().compareTo(((DatedObject) o).getDate());
		}
		return Integer.compare(getId(), o.getId());
	}
	
	@Override
	@Nonnull
	public URL getUrl(){
		return url;
	}
	
	@Nonnull
	public Media getMedia(){
		return media;
	}
	
	@Override
	public int hashCode(){
		return this.getId();
	}
	
	@Nonnull
	public static String getQuery(){
		return QUERY;
	}
}
