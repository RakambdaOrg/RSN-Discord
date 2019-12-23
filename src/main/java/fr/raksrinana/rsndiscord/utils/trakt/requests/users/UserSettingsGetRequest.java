package fr.raksrinana.rsndiscord.utils.trakt.requests.users;

import fr.raksrinana.rsndiscord.utils.trakt.TraktGetRequest;
import fr.raksrinana.rsndiscord.utils.trakt.model.users.settings.UserSettings;
import kong.unirest.GenericType;
import lombok.NonNull;
import java.util.Map;

public class UserSettingsGetRequest implements TraktGetRequest<UserSettings>{
	@Override
	public @NonNull String getEndpoint(){
		return "/users/settings";
	}
	
	@Override
	public GenericType<? extends UserSettings> getResultClass(){
		return new GenericType<>(){};
	}
	
	@Override
	public Map<String, String> getParameters(){
		return null;
	}
}
