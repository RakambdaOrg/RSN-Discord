package fr.mrcraftcod.gunterdiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
@SuppressWarnings("WeakerAccess")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("MANGA")
public class AniListMangaMedia extends AniListMedia{
	@JsonProperty("chapters")
	private Integer chapters;
	@JsonProperty("volumes")
	private Integer volumes;
	
	public AniListMangaMedia(){
		super(AniListMediaType.MANGA);
	}
	
	@Override
	@Nonnull
	public String getProgressType(final boolean contains){
		return "read chapter";
	}
	
	@Override
	public void fillEmbed(@Nonnull final EmbedBuilder builder){
		super.fillEmbed(builder);
		Optional.ofNullable(this.getChapters()).map(Object::toString).ifPresent(val -> builder.addField("Chapters", val, true));
		Optional.ofNullable(this.getVolumes()).map(Object::toString).ifPresent(val -> builder.addField("Volumes", val, true));
	}
	
	@Override
	@Nullable
	public Integer getItemCount(){
		return this.getChapters();
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	/**
	 * Get the number of chapters for this manga.
	 *
	 * @return The number of chapters.
	 */
	@Nullable
	public Integer getChapters(){
		return this.chapters;
	}
	
	/**
	 * Get the number of volumes for this manga.
	 *
	 * @return The number of volumes.
	 */
	@Nullable
	public Integer getVolumes(){
		return this.volumes;
	}
}
