package fr.rakambda.rsndiscord.spring.api.trakt.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.rsndiscord.spring.api.trakt.response.data.settings.Account;
import fr.rakambda.rsndiscord.spring.api.trakt.response.data.settings.Connections;
import fr.rakambda.rsndiscord.spring.api.trakt.response.data.settings.SharingText;
import fr.rakambda.rsndiscord.spring.api.trakt.response.data.settings.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSettingsResponse{
	private User user;
	private Account account;
	private Connections connections;
	@JsonProperty("sharing_text")
	private SharingText sharingText;
}
