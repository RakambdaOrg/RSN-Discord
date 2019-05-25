package fr.mrcraftcod.gunterdiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.*;
import fr.mrcraftcod.gunterdiscord.utils.anilist.AniListObject;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
@SuppressWarnings("WeakerAccess")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = AniListAnimeMedia.class, name = "ANIME"),
		@JsonSubTypes.Type(value = AniListMangaMedia.class, name = "MANGA")
})
public abstract class AniListMedia implements AniListObject{
	private static final String QUERY = "media {\n" + "id\n" + "title {\n" + "userPreferred\n" + "}\n" + "season\n" + "type\n" + "format\n" + "status\n" + "episodes\n" + "chapters\n" + "volumes\n" + "isAdult\n" + "coverImage{\n" + "large\n" + "}\n" + "siteUrl" + "}";
	
	private final AniListMediaType type;
	@JsonProperty("title")
	private AniListMediaTitle title;
	@JsonProperty("season")
	private AniListMediaSeason season;
	@JsonProperty("format")
	private AniListMediaFormat format;
	@JsonProperty("status")
	private AniListMediaStatus status;
	@JsonProperty("siteUrl")
	private URL url;
	@JsonProperty("coverImage")
	private AniListCoverImage coverImage;
	@JsonProperty("isAdult")
	private boolean isAdult;
	@JsonProperty("id")
	private int id;
	
	protected AniListMedia(final AniListMediaType type){
		this.type = type;
	}
	
	@Override
	public boolean equals(final Object obj){
		return obj instanceof AniListMedia && Objects.equals(((AniListMedia) obj).getId(), getId());
	}
	
	public abstract String getProgressType(final boolean contains);
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	public abstract Integer getItemCount();
	
	@Override
	public void fillEmbed(final EmbedBuilder builder){
		builder.setDescription(getTitle().getUserPreferred());
		if(getType().shouldDisplay()){
			builder.addField("Type", getType().toString(), true);
		}
		builder.addField("Format", Optional.ofNullable(getFormat()).map(Enum::toString).orElse("UNKNOWN"), true);
		builder.addField("Status", Optional.ofNullable(getStatus()).map(Enum::toString).orElse("UNKNOWN"), true);
		if(isAdult()){
			builder.addField("Adult content", "", true);
		}
		//builder.addField("Link", getUrl(), false);
		builder.setThumbnail(getCoverImage().getLarge().toString());
	}
	
	@Override
	public int getId(){
		return this.id;
	}
	
	public AniListMediaTitle getTitle(){
		return this.title;
	}
	
	public static String getQuery(){
		return QUERY;
	}
	
	public AniListMediaType getType(){
		return this.type;
	}
	
	public AniListMediaFormat getFormat(){
		return this.format;
	}
	
	public AniListMediaStatus getStatus(){
		return this.status;
	}
	
	public boolean isAdult(){
		return this.isAdult;
	}
	
	public AniListCoverImage getCoverImage(){
		return this.coverImage;
	}
	
	@Override
	public URL getUrl(){
		return this.url;
	}
	
	public AniListMediaSeason getSeason(){
		return this.season;
	}
	
	@Override
	public int hashCode(){
		return this.getId();
	}
	
	@Override
	public int compareTo(@NotNull final AniListObject o){
		return Integer.compare(getId(), o.getId());
	}
}
