package fr.raksrinana.rsndiscord.api.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import static fr.raksrinana.rsndiscord.api.anilist.data.media.MediaType.MANGA;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Optional.ofNullable;

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
		super(MANGA);
	}
	
	@Override
	protected void fillAdditionalEmbed(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		ofNullable(getChapters()).map(Object::toString).ifPresent(val -> builder.addField(translate(guild, "anilist.chapters"), val, true));
		ofNullable(getVolumes()).map(Object::toString).ifPresent(val -> builder.addField(translate(guild, "anilist.volumes"), val, true));
	}
	
	@Override
	@NotNull
	public String getProgressType(boolean contains){
		return "read chapter";
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	@Nullable
	public Integer getItemCount(){
		return getChapters();
	}
}
