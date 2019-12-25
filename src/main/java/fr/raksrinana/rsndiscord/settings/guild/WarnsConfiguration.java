package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.guild.warns.WarnConfiguration;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class WarnsConfiguration{
	@JsonProperty("simpleWarn")
	@Setter
	private WarnConfiguration simpleWarn;
	@JsonProperty("doubleWarn")
	@Setter
	private WarnConfiguration doubleWarn;
	@JsonProperty("megaWarn")
	@Setter
	private WarnConfiguration megaWarn;
	
	@NonNull
	public Optional<WarnConfiguration> getDoubleWarn(){
		return Optional.ofNullable(this.doubleWarn);
	}
	
	@NonNull
	public Optional<WarnConfiguration> getMegaWarn(){
		return Optional.ofNullable(this.megaWarn);
	}
	
	@NonNull
	public Optional<WarnConfiguration> getSimpleWarn(){
		return Optional.ofNullable(this.simpleWarn);
	}
}
