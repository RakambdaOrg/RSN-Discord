package fr.raksrinana.rsndiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.*;
import fr.raksrinana.rsndiscord.utils.anilist.AniListObject;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
	
	protected AniListMedia(@Nonnull final AniListMediaType type){
		this.type = type;
	}
	
	@Override
	public boolean equals(@Nullable final Object obj){
		return obj instanceof AniListMedia && Objects.equals(((AniListObject) obj).getId(), this.getId());
	}
	
	@Nonnull
	public abstract String getProgressType(final boolean contains);
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	public void fillEmbed(@Nonnull final EmbedBuilder builder){
		builder.setDescription(this.getTitle().getUserPreferred());
		if(this.getType().shouldDisplay()){
			builder.addField("Type", this.getType().toString(), true);
		}
		builder.addField("Format", Optional.of(this.getFormat()).map(Enum::toString).orElse("UNKNOWN"), true);
		builder.addField("Status", Optional.of(this.getStatus()).map(Enum::toString).orElse("UNKNOWN"), true);
		if(this.isAdult()){
			builder.addField("Adult content", "", true);
		}
		//builder.addField("Link", getUrl(), false);
		builder.setThumbnail(this.getCoverImage().getLarge().toString());
	}
	
	@Nonnull
	public AniListMediaTitle getTitle(){
		return this.title;
	}
	
	@Override
	public int getId(){
		return this.id;
	}
	
	@Nonnull
	public AniListMediaType getType(){
		return this.type;
	}

	@Nonnull
	public AniListMediaFormat getFormat(){
		return this.format;
	}

	@Nonnull
	public AniListMediaStatus getStatus(){
		return this.status;
	}

	@Nonnull
	public AniListCoverImage getCoverImage(){
		return this.coverImage;
	}

	@Override
	@Nonnull
	public URL getUrl(){
		return this.url;
	}
	
	public boolean isAdult(){
		return this.isAdult;
	}

	@Override
	public int compareTo(@Nonnull final AniListObject o){
		return Integer.compare(this.getId(), o.getId());
	}
	
	@Nullable
	public abstract Integer getItemCount();

	@Nonnull
	public static String getQuery(){
		return QUERY;
	}
	
	@Override
	public int hashCode(){
		return this.getId();
	}
	
	@Nullable
	public AniListMediaSeason getSeason(){
		return this.season;
	}
}
