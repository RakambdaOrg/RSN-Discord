package fr.raksrinana.rsndiscord.modules.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.modules.settings.general.AniListGeneral;
import fr.raksrinana.rsndiscord.modules.settings.general.HermitcraftGeneral;
import fr.raksrinana.rsndiscord.modules.settings.general.TraktGeneral;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class GeneralConfiguration implements ICompositeConfiguration{
	@JsonProperty("anilist")
	@Getter
	private final AniListGeneral aniList = new AniListGeneral();
	@JsonProperty("trakt")
	@Getter
	private final TraktGeneral trakt = new TraktGeneral();
	@JsonProperty("hermitcraft")
	@Getter
	private final HermitcraftGeneral hermitcraft = new HermitcraftGeneral();
}
