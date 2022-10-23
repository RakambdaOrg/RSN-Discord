package fr.rakambda.rsndiscord.spring.api.trakt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.rsndiscord.spring.json.converter.SQLTimestampDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenResponse{
	@JsonProperty("access_token")
	private String accessToken;
	@JsonProperty("token_type")
	private String tokenType;
	@JsonProperty("expires_in")
	private int expiresIn;
	@JsonProperty("refresh_token")
	private String refreshToken;
	private String scope;
	@JsonProperty("created_at")
	@JsonDeserialize(using = SQLTimestampDeserializer.class)
	private ZonedDateTime createdAt;
}
