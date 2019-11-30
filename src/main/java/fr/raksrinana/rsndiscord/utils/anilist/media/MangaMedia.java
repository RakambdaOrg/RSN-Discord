package fr.raksrinana.rsndiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("MANGA")
@Getter
public class MangaMedia extends Media{
	@JsonProperty("chapters")
	private Integer chapters;
	@JsonProperty("volumes")
	private Integer volumes;
	
	public MangaMedia(){
		super(MediaType.MANGA);
	}
	
	@Override
	@NonNull
	public String getProgressType(final boolean contains){
		return "read chapter";
	}
	
	@Override
	protected void fillAdditionalEmbed(EmbedBuilder builder){
		Optional.ofNullable(this.getChapters()).map(Object::toString).ifPresent(val -> builder.addField("Chapters", val, true));
		Optional.ofNullable(this.getVolumes()).map(Object::toString).ifPresent(val -> builder.addField("Volumes", val, true));
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	public Integer getItemCount(){
		return this.getChapters();
	}
}
