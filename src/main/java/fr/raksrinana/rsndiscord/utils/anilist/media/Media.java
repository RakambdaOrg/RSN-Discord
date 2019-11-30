package fr.raksrinana.rsndiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.*;
import fr.raksrinana.rsndiscord.utils.anilist.AniListObject;
import fr.raksrinana.rsndiscord.utils.anilist.FuzzyDate;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = AnimeMedia.class, name = "ANIME"),
		@JsonSubTypes.Type(value = MangaMedia.class, name = "MANGA")
})
@Getter
public abstract class Media implements AniListObject{
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	@Getter
	private static final String QUERY = "media {\n" + "id\n" + MediaTitle.getQUERY() + "\n" + "season\n" + "type\n" + "format\n" + "status\n" + "episodes\n" + "chapters\n" + "volumes\n" + FuzzyDate.getQuery("startDate") + "\n" + FuzzyDate.getQuery("endDate") + "\n" + "genres\n" + "isAdult\n" + MediaCoverImage.getQUERY() + "\n" + "siteUrl" + "}";
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
	@JsonProperty("startDate")
	private FuzzyDate startDate = new FuzzyDate();
	@JsonProperty("endDate")
	private FuzzyDate endDate = new FuzzyDate();
	@JsonProperty("genres")
	private Set<String> genres = new HashSet<>();
	@JsonProperty("id")
	private int id;
	
	protected Media(@NonNull final MediaType type){
		this.type = type;
	}
	
	@NonNull
	public abstract String getProgressType(final boolean contains);
	
	@Override
	public void fillEmbed(@NonNull final EmbedBuilder builder){
		builder.setDescription(this.getTitle().getRomaji());
		if(this.getType().isShouldDisplay()){
			builder.addField("Type", this.getType().toString(), true);
		}
		builder.addField("Format", Optional.of(this.getFormat()).map(Enum::toString).orElse("UNKNOWN"), true);
		builder.addField("Status", Optional.of(this.getStatus()).map(Enum::toString).orElse("UNKNOWN"), true);
		if(this.isAdult()){
			builder.addField("Adult content", "", true);
		}
		fillAdditionalEmbed(builder);
		this.getStartDate().asDate().ifPresent(startDate -> builder.addField("Started releasing", startDate.format(DF), false));
		this.getEndDate().asDate().ifPresent(startDate -> builder.addField("Ended releasing", startDate.format(DF), false));
		if(!genres.isEmpty()){
			builder.addField("Genres", String.join(", ", getGenres()), false);
		}
		//builder.addField("Link", getUrl(), false);
		builder.setThumbnail(this.getCoverImage().getLarge().toString());
		builder.setFooter(Integer.toString(getId()));
	}
	
	protected abstract void fillAdditionalEmbed(EmbedBuilder builder);
	
	@Override
	public int hashCode(){
		return this.getId();
	}
	
	@Override
	public boolean equals(final Object obj){
		return obj instanceof Media && Objects.equals(((Media) obj).getId(), this.getId());
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	public int compareTo(@NonNull final AniListObject o){
		return Integer.compare(this.getId(), o.getId());
	}
	
	public abstract Integer getItemCount();
}
