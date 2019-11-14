package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.guild.warns.WarnConfiguration;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class WarnsConfiguration{
	@JsonProperty("simpleWarn")
	private WarnConfiguration simpleWarn;
	@JsonProperty("doubleWarn")
	private WarnConfiguration doubleWarn;
	@JsonProperty("megaWarn")
	private WarnConfiguration megaWarn;
	
	@NonNull
	public Optional<WarnConfiguration> getDoubleWarn(){
		return Optional.ofNullable(this.doubleWarn);
	}
	
	public void setDoubleWarn(final WarnConfiguration doubleWarn){
		this.doubleWarn = doubleWarn;
	}
	
	@NonNull
	public Optional<WarnConfiguration> getMegaWarn(){
		return Optional.ofNullable(this.megaWarn);
	}
	
	public void setMegaWarn(final WarnConfiguration megaWarn){
		this.megaWarn = megaWarn;
	}
	
	@NonNull
	public Optional<WarnConfiguration> getSimpleWarn(){
		return Optional.ofNullable(this.simpleWarn);
	}
	
	public void setSimpleWarn(final WarnConfiguration simpleWarn){
		this.simpleWarn = simpleWarn;
	}
}
