package fr.raksrinana.rsndiscord.settings.impl.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.api.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@Slf4j
public class TraktConfiguration implements ICompositeConfiguration{
	@JsonProperty("mediaChangeChannel")
	@Setter
	private ChannelConfiguration mediaChangeChannel;
	@JsonProperty("thaChannel")
	@Setter
	private ChannelConfiguration thaChannel;
	@JsonProperty("thaUser")
	@Setter
	private UserConfiguration thaUser;
	
	@NotNull
	public Optional<ChannelConfiguration> getMediaChangeChannel(){
		return ofNullable(mediaChangeChannel);
	}
	
	@NotNull
	public Optional<ChannelConfiguration> getThaChannel(){
		return ofNullable(thaChannel);
	}
	
	@NotNull
	public Optional<UserConfiguration> getThaUser(){
		return ofNullable(thaUser);
	}
}
