package fr.rakambda.rsndiscord.spring.api.simkl.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenResponse {
	private String result;
	private String message;
	@JsonProperty("access_token")
	private String accessToken;
}
