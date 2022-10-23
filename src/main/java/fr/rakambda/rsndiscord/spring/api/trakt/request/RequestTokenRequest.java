package fr.rakambda.rsndiscord.spring.api.trakt.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestTokenRequest{
	@JsonProperty("client_id")
	private String clientId;
	@JsonProperty("client_secret")
	private String clientSecret;
	private String code;
}
