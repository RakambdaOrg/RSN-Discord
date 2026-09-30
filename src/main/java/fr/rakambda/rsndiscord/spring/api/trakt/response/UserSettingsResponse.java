package fr.rakambda.rsndiscord.spring.api.trakt.response;

import fr.rakambda.rsndiscord.spring.api.trakt.response.data.settings.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSettingsResponse{
	private User user;
}
