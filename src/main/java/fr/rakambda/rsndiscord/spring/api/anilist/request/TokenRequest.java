package fr.rakambda.rsndiscord.spring.api.anilist.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenRequest{
	@JsonProperty("grant_type")
	private String grantType;
	@JsonProperty("client_id")
	private String clientId;
	@JsonProperty("client_secret")
	private String clientSecret;
	@JsonProperty("redirect_uri")
	private String redirectUri;
	@JsonProperty("code")
	private String code;
}
