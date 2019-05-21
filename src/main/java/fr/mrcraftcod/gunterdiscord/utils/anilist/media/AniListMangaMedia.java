package fr.mrcraftcod.gunterdiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
public class AniListMangaMedia extends AniListMedia{
	@JsonProperty("chapters")
	private Integer chapters;
	@JsonProperty("volumes")
	private Integer volumes;
	
	public AniListMangaMedia(){
		super(AniListMediaType.MANGA);
	}
	
	@Override
	public void fillEmbed(final EmbedBuilder builder){
		super.fillEmbed(builder);
		Optional.ofNullable(getChapters()).map(Object::toString).ifPresent(val -> builder.addField("Chapters", val, true));
		Optional.ofNullable(getVolumes()).map(Object::toString).ifPresent(val -> builder.addField("Volumes", val, true));
	}
	
	/**
	 * Get the number of chapters for this manga.
	 *
	 * @return The number of chapters.
	 */
	public Integer getChapters(){
		return this.chapters;
	}
	
	@Override
	public String getProgressType(final boolean contains){
		return "read chapter";
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	public Integer getItemCount(){
		return getChapters();
	}
	
	/**
	 * Get the number of volumes for this manga.
	 *
	 * @return The number of volumes.
	 */
	public Integer getVolumes(){
		return this.volumes;
	}
}
