package fr.rakambda.rsndiscord.spring.api.trakt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceCodeResponse{
	@JsonProperty("device_code")
	private String deviceCode;
	@JsonProperty("user_code")
	private String userCode;
	@JsonProperty("verification_url")
	private String verificationUrl;
	@JsonProperty("expires_in")
	private int expiresIn;
	private int interval;
}
