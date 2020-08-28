package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.CompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;

@SuppressWarnings("FieldMayBeFinal")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@Slf4j
public class TraktConfiguration implements CompositeConfiguration{
	@JsonProperty("mediaChangeChannel")
	@Setter
	private ChannelConfiguration mediaChangeChannel;
	@JsonProperty("thaChannel")
	@Setter
	private ChannelConfiguration thaChannel;
	@JsonProperty("thaUser")
	@Setter
	private UserConfiguration thaUser;
	
	@NonNull
	public Optional<ChannelConfiguration> getMediaChangeChannel(){
		return Optional.ofNullable(this.mediaChangeChannel);
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getThaChannel(){
		return Optional.ofNullable(this.thaChannel);
	}
	
	@NonNull
	public Optional<UserConfiguration> getThaUser(){
		return Optional.ofNullable(this.thaUser);
	}
}
