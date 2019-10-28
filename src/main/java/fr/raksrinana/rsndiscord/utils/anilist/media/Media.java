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
		@JsonSubTypes.Type(value = AnimeMedia.class, name = "ANIME"),
		@JsonSubTypes.Type(value = MangaMedia.class, name = "MANGA")
})
public abstract class Media implements AniListObject{
	private static final String QUERY = "media {\n" + "id\n" + MediaTitle.getQuery() + "\n" + "season\n" + "type\n" + "format\n" + "status\n" + "episodes\n" + "chapters\n" + "volumes\n" + "isAdult\n" + MediaCoverImage.getQuery() + "\n" + "siteUrl" + "}";
	private final MediaType type;
	@JsonProperty("title")
	private MediaTitle title;
	@JsonProperty("season")
	private MediaSeason season;
	@JsonProperty("format")
	private MediaFormat format;
	@JsonProperty("status")
	private MediaStatus status;
	@JsonProperty("siteUrl")
	private URL url;
	@JsonProperty("coverImage")
	private MediaCoverImage coverImage;
	@JsonProperty("isAdult")
	private boolean isAdult;
	@JsonProperty("id")
	private int id;
	
	protected Media(@Nonnull final MediaType type){
		this.type = type;
	}
	
	@Override
	public boolean equals(@Nullable final Object obj){
		return obj instanceof Media && Objects.equals(((Media) obj).getId(), this.getId());
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
	public MediaCoverImage getCoverImage(){
		return this.coverImage;
	}
	
	@Override
	public int getId(){
		return this.id;
	}
	
	@Nonnull
	public MediaFormat getFormat(){
		return this.format;
	}
	
	@Nullable
	public MediaSeason getSeason(){
		return this.season;
	}
	
	@Nonnull
	public MediaStatus getStatus(){
		return this.status;
	}
	
	@Nonnull
	public MediaTitle getTitle(){
		return this.title;
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
	
	@Nonnull
	public MediaType getType(){
		return this.type;
	}
}
