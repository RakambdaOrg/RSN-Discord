package fr.raksrinana.rsndiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.raksrinana.rsndiscord.utils.anilist.FuzzyDate;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.time.LocalDate;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("ANIME")
@Getter
public class AnimeMedia extends Media{
	@JsonProperty("episodes")
	private Integer episodes;
	
	public AnimeMedia(){
		super(MediaType.ANIME);
	}
	
	@Override
	protected void fillAdditionalEmbed(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		final var year = Optional.ofNullable(this.getStartDate()).flatMap(FuzzyDate::asDate).map(LocalDate::getYear);
		Optional.ofNullable(this.getEpisodes()).map(Object::toString).ifPresent(val -> builder.addField(translate(guild, "anilist.episodes"), val, true));
		Optional.ofNullable(this.getSeason()).map(Enum::toString).ifPresent(val -> builder.addField(translate(guild, "anilist.season"), val + year.map(y -> " " + y).orElse(""), true));
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
