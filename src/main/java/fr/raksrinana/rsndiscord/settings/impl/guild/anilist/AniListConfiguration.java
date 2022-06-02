package fr.raksrinana.rsndiscord.settings.impl.guild.anilist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.api.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class AniListConfiguration implements ICompositeConfiguration{
	@JsonProperty("notificationsChannel")
	@Setter
	private ChannelConfiguration notificationsChannel;
	@JsonProperty("mediaChangeChannel")
	@Setter
	private ChannelConfiguration mediaChangeChannel;
	
	@NotNull
	public Optional<ChannelConfiguration> getMediaChangeChannel(){
		return ofNullable(mediaChangeChannel);
	}
	
	@NotNull
	public Optional<ChannelConfiguration> getNotificationsChannel(){
		return ofNullable(notificationsChannel);
	}
}
