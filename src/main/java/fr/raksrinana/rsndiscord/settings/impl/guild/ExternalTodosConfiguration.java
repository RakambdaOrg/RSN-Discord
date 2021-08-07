package fr.raksrinana.rsndiscord.settings.impl.guild;

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
public class ExternalTodosConfiguration implements ICompositeConfiguration{
	@JsonProperty("notificationChannel")
	@Setter
	private ChannelConfiguration notificationChannel;
	@JsonProperty("endpoint")
	@Setter
	private String endpoint;
	@JsonProperty("token")
	@Setter
	private String token;
	
	@NotNull
	public Optional<String> getEndpoint(){return ofNullable(endpoint);}
	
	@NotNull
	public Optional<ChannelConfiguration> getNotificationChannel(){
		return ofNullable(notificationChannel);
	}
	
	@NotNull
	public Optional<String> getToken(){
		return ofNullable(token);
	}
}
