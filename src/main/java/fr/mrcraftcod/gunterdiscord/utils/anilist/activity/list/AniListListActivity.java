package fr.mrcraftcod.gunterdiscord.utils.anilist.activity.list;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListDatedObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListObject;
import fr.mrcraftcod.gunterdiscord.utils.anilist.media.AniListAnimeMedia;
import fr.mrcraftcod.gunterdiscord.utils.anilist.media.AniListMangaMedia;
import fr.mrcraftcod.gunterdiscord.utils.anilist.media.AniListMedia;
import fr.mrcraftcod.gunterdiscord.utils.json.SQLTimestampDeserializer;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.net.URL;
import java.util.Date;
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
		@JsonSubTypes.Type(value = AniListAnimeMedia.class, name = "ANIME_LIST"),
		@JsonSubTypes.Type(value = AniListMangaMedia.class, name = "MANGA_LIST")
})
public abstract class AniListListActivity implements AniListDatedObject{
	private static final String QUERY = "ListActivity{\n" + "id\n" + "userId\n" + "type\n" + "createdAt\n" + "progress\n" + "siteUrl\n" + AniListMedia.getQuery() + "}";
	
	@JsonProperty("createdAt")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private Date createdAt;
	@JsonProperty("siteUrl")
	private URL url;
	@JsonProperty("progress")
	private String progress;
	@JsonProperty("media")
	private AniListMedia media;
	@JsonProperty("id")
	private int id;
	
	@Override
	public boolean equals(@Nullable final Object obj){
		return obj instanceof AniListListActivity && Objects.equals(((AniListListActivity) obj).getId(), getId());
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	public void fillEmbed(@Nonnull final EmbedBuilder builder){
		builder.setColor(getColor());
		builder.setTimestamp(getDate().toInstant());
		
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
	public Date getDate(){
		return createdAt;
	}
	
	@Nonnull
	public String getProgress(){
		return progress;
	}
	
	@Nonnull
	public AniListMedia getMedia(){
		return media;
	}
	
	@Override
	@Nonnull
	public URL getUrl(){
		return url;
	}
	
	@Override
	public int compareTo(@Nonnull final AniListObject o){
		if(o instanceof AniListDatedObject){
			return getDate().compareTo(((AniListDatedObject) o).getDate());
		}
		return Integer.compare(getId(), o.getId());
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
