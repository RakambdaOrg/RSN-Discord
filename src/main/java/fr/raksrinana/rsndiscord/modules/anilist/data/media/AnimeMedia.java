package fr.raksrinana.rsndiscord.modules.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.rsndiscord.modules.anilist.data.FuzzyDate;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.time.LocalDate;
import static fr.raksrinana.rsndiscord.modules.anilist.data.media.MediaType.ANIME;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("ANIME")
@Getter
public class AnimeMedia extends IMedia{
	@JsonProperty("episodes")
	private Integer episodes;
	
	public AnimeMedia(){
		super(ANIME);
	}
	
	@Override
	protected void fillAdditionalEmbed(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		final var year = ofNullable(this.getStartDate())
				.flatMap(FuzzyDate::asDate)
				.map(LocalDate::getYear);
		ofNullable(this.getEpisodes()).map(Object::toString)
				.ifPresent(val -> builder.addField(translate(guild, "anilist.episodes"), val, true));
		ofNullable(this.getSeason()).map(Enum::toString)
				.ifPresent(val -> builder.addField(translate(guild, "anilist.season"), val + year.map(y -> " " + y).orElse(""), true));
	}
	
	@Override
	@NonNull
	public String getProgressType(final boolean contains){
		return "watched episode";
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
	
	@Override
	public Integer getItemCount(){
		return this.getEpisodes();
	}
}
