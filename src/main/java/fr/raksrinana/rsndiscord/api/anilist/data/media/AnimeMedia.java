package fr.raksrinana.rsndiscord.api.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.rsndiscord.api.anilist.data.FuzzyDate;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.LocalDate;
import static fr.raksrinana.rsndiscord.api.anilist.data.media.MediaType.ANIME;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("ANIME")
@Getter
public class AnimeMedia extends Media{
	@JsonProperty("episodes")
	private Integer episodes;
	
	public AnimeMedia(){
		super(ANIME);
	}
	
	@Override
	protected void fillAdditionalEmbed(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		var year = ofNullable(getStartDate())
				.flatMap(FuzzyDate::asDate)
				.map(LocalDate::getYear);
		ofNullable(getEpisodes()).map(Object::toString)
				.ifPresent(val -> builder.addField(translate(guild, "anilist.episodes"), val, true));
		ofNullable(getSeason()).map(Enum::toString)
				.ifPresent(val -> builder.addField(translate(guild, "anilist.season"), val + year.map(y -> " " + y).orElse(""), true));
	}
	
	@Override
	@NotNull
	public String getProgressType(boolean contains){
		return "watched episode";
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	@Nullable
	public Integer getItemCount(){
		return getEpisodes();
	}
}
