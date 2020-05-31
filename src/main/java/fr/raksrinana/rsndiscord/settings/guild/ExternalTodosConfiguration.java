package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.CompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class ExternalTodosConfiguration implements CompositeConfiguration{
	@JsonProperty("notificationChannel")
	@Setter
	private ChannelConfiguration notificationChannel;
	@JsonProperty("endpoint")
	@Setter
	private String endpoint;
	@JsonProperty("token")
	@Setter
	private String token;
	
	@NonNull
	public Optional<ChannelConfiguration> getNotificationChannel(){
		return Optional.ofNullable(this.notificationChannel);
	}
	
	@NonNull
	public Optional<String> getEndpoint(){return Optional.ofNullable(this.endpoint);}
	
	@NonNull
	public Optional<String> getToken(){return Optional.ofNullable(this.token);}
}
