package fr.mrcraftcod.gunterdiscord.newSettings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.newSettings.guild.warns.WarnConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.*;
import net.dv8tion.jda.api.entities.Guild;
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
	
	public void mapOldConf(Guild guild){
		new WarnRoleConfig(guild).getObject().ifPresent(role -> new WarnTimeConfig(guild).getObject().ifPresent(delay -> this.setSimpleWarn(new WarnConfiguration(role, Math.round(delay * 24 * 3600)))));
		new DoubleWarnRoleConfig(guild).getObject().ifPresent(role -> new DoubleWarnTimeConfig(guild).getObject().ifPresent(delay -> this.setDoubleWarn(new WarnConfiguration(role, Math.round(delay * 24 * 3600)))));
		new MegaWarnRoleConfig(guild).getObject().ifPresent(role -> new MegaWarnTimeConfig(guild).getObject().ifPresent(delay -> this.setMegaWarn(new WarnConfiguration(role, Math.round(delay * 24 * 3600)))));
	}
	
	@Nonnull
	public Optional<WarnConfiguration> getSimpleWarn(){
		return Optional.ofNullable(simpleWarn);
	}
	
	public void setSimpleWarn(@Nullable WarnConfiguration simpleWarn){
		this.simpleWarn = simpleWarn;
	}
	
	@Nonnull
	public Optional<WarnConfiguration> getDoubleWarn(){
		return Optional.ofNullable(doubleWarn);
	}
	
	public void setDoubleWarn(@Nullable WarnConfiguration doubleWarn){
		this.doubleWarn = doubleWarn;
	}
	
	@Nonnull
	public Optional<WarnConfiguration> getMegaWarn(){
		return Optional.ofNullable(megaWarn);
	}
	
	public void setMegaWarn(@Nullable WarnConfiguration megaWarn){
		this.megaWarn = megaWarn;
	}
}
