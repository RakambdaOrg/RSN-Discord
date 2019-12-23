package fr.raksrinana.rsndiscord.utils.trakt.model.users.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class UserSettings{
	@JsonProperty("user")
	private User user;
	@JsonProperty("account")
	private Account account;
	@JsonProperty("connections")
	private Connections connections;
	@JsonProperty("sharing_text")
	private SharingText sharingText;
}
