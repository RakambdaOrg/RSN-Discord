package fr.raksrinana.rsndiscord.modules.anilist.data.media;

import com.fasterxml.jackson.annotation.*;
import fr.raksrinana.rsndiscord.modules.anilist.data.FuzzyDate;
import fr.raksrinana.rsndiscord.modules.anilist.data.IAniListObject;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = AnimeMedia.class, name = "ANIME"),
		@JsonSubTypes.Type(value = MangaMedia.class, name = "MANGA")
})
@Getter
public abstract class IMedia implements IAniListObject{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	@Getter
	private static final String QUERY = """
			media {
			    id
			    season
			    type
			    format
			    status
			    episodes
			    chapters
			    volumes
			    genres
			    isAdult
			    siteUrl
			    %s
			    %s
			    %s
			    %s
			}
			""".formatted(
			MediaTitle.getQUERY(),
			FuzzyDate.getQuery("startDate"),
			FuzzyDate.getQuery("endDate"),
			MediaCoverImage.getQUERY()
	);
	private final MediaType type;
	@JsonProperty("startDate")
	private final FuzzyDate startDate = new FuzzyDate();
	@JsonProperty("endDate")
	private final FuzzyDate endDate = new FuzzyDate();
	@JsonProperty("genres")
	private final Set<String> genres = new HashSet<>();
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
	
	protected IMedia(@NonNull final MediaType type){
		this.type = type;
	}
	
	@Override
	public void fillEmbed(@NonNull Guild guild, @NonNull final EmbedBuilder builder){
		builder.setDescription(getTitle().getRomaji());
		if(getType().isShouldDisplay()){
			builder.addField(translate(guild, "anilist.type"), getType().toString(), true);
		}
		builder.addField(translate(guild, "anilist.format"), ofNullable(getFormat()).map(Enum::toString).orElse("-"), true)
				.addField(translate(guild, "anilist.status"), ofNullable(getStatus()).map(Enum::toString).orElse("-"), true);
		if(isAdult()){
			builder.addField(translate(guild, "anilist.adult"), "", true);
		}
		
		fillAdditionalEmbed(guild, builder);
		
		getStartDate().asDate()
				.ifPresent(startDate -> builder.addField(translate(guild, "anilist.started"), startDate.format(DF), true));
		getEndDate().asDate()
				.ifPresent(startDate -> builder.addField(translate(guild, "anilist.ended"), startDate.format(DF), true));
		
		if(!genres.isEmpty()){
			builder.addField(translate(guild, "anilist.genres"), String.join(", ", getGenres()), true);
		}
		builder.setThumbnail(getCoverImage().getLarge().toString())
				.setFooter("ID: " + getId());
	}
	
	protected abstract void fillAdditionalEmbed(@NonNull Guild guild, @NonNull EmbedBuilder builder);
	
	@NonNull
	public abstract String getProgressType(final boolean contains);
	
	@Override
	public int hashCode(){
		return getId();
	}
	
	@Override
	public boolean equals(final Object obj){
		return obj instanceof IMedia && Objects.equals(((IMedia) obj).getId(), getId());
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	public int compareTo(@NonNull final IAniListObject o){
		return Integer.compare(getId(), o.getId());
	}
	
	public abstract Integer getItemCount();
	
	public static String getQueryWithId(){
		return "media(id: $mediaId) {\n" + "id\n" + MediaTitle.getQUERY() + "\n" + "season\n" + "type\n" + "format\n" + "status\n" + "episodes\n" + "chapters\n" + "volumes\n" + FuzzyDate.getQuery("startDate") + "\n" + FuzzyDate.getQuery("endDate") + "\n" + "genres\n" + "isAdult\n" + MediaCoverImage.getQUERY() + "\n" + "siteUrl" + "}";
	}
}
