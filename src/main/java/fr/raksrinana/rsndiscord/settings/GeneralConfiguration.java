package fr.raksrinana.rsndiscord.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.general.AniListGeneral;
import fr.raksrinana.rsndiscord.settings.general.HermitcraftGeneral;
import fr.raksrinana.rsndiscord.settings.general.TraktGeneral;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class GeneralConfiguration implements CompositeConfiguration{
	@JsonProperty("anilist")
	@Getter
	private AniListGeneral aniList = new AniListGeneral();
	@JsonProperty("trakt")
	@Getter
	private TraktGeneral trakt = new TraktGeneral();
	@JsonProperty("hermitcraft")
	@Getter
	private HermitcraftGeneral hermitcraft = new HermitcraftGeneral();
}
