package fr.raksrinana.rsndiscord.utils.trakt.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class DeviceCode{
	@JsonProperty("device_code")
	private String deviceCode;
	@JsonProperty("user_code")
	private String userCode;
	@JsonProperty("verification_url")
	private String verificationUrl;
	@JsonProperty("expires_in")
	private int expiresIn;
	@JsonProperty("interval")
	private int interval;
}
