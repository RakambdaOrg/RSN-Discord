package fr.rakambda.rsndiscord.spring.api.anilist.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class TokenResponse{
	private String error;
	@JsonProperty("access_token")
	private String accessToken;
	@JsonProperty("refresh_token")
	private String refreshToken;
	@JsonProperty("expires_in")
	private int expiresIn = 0;
}
