package fr.raksrinana.rsndiscord.modules.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static fr.raksrinana.rsndiscord.modules.anilist.data.media.MediaType.MANGA;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("MANGA")
@Getter
public class MangaMedia extends IMedia{
	@JsonProperty("chapters")
	private Integer chapters;
	@JsonProperty("volumes")
	private Integer volumes;
	
	public MangaMedia(){
		super(MANGA);
	}
	
	@Override
	protected void fillAdditionalEmbed(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		ofNullable(this.getChapters()).map(Object::toString)
				.ifPresent(val -> builder.addField(translate(guild, "anilist.chapters"), val, true));
		ofNullable(this.getVolumes()).map(Object::toString)
				.ifPresent(val -> builder.addField(translate(guild, "anilist.volumes"), val, true));
	}
	
	@Override
	@NonNull
	public String getProgressType(final boolean contains){
		return "read chapter";
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
