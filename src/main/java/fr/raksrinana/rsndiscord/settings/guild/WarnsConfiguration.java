package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.guild.warns.WarnConfiguration;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarnsConfiguration{
	@JsonProperty("simpleWarn")
	private WarnConfiguration simpleWarn;
	@JsonProperty("doubleWarn")
	private WarnConfiguration doubleWarn;
	@JsonProperty("megaWarn")
	private WarnConfiguration megaWarn;
	
	public WarnsConfiguration(){
	}
	
	@Nonnull
	public Optional<WarnConfiguration> getDoubleWarn(){
		return Optional.ofNullable(this.doubleWarn);
	}
	
	public void setDoubleWarn(@Nullable final WarnConfiguration doubleWarn){
		this.doubleWarn = doubleWarn;
	}
	
	@Nonnull
	public Optional<WarnConfiguration> getMegaWarn(){
		return Optional.ofNullable(this.megaWarn);
	}
	
	public void setMegaWarn(@Nullable final WarnConfiguration megaWarn){
		this.megaWarn = megaWarn;
	}
	
	@Nonnull
	public Optional<WarnConfiguration> getSimpleWarn(){
		return Optional.ofNullable(this.simpleWarn);
	}
	
	public void setSimpleWarn(@Nullable final WarnConfiguration simpleWarn){
		this.simpleWarn = simpleWarn;
	}
}
