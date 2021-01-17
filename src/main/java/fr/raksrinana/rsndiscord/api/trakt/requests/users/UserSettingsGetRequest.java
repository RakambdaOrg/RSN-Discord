package fr.raksrinana.rsndiscord.api.trakt.requests.users;

import fr.raksrinana.rsndiscord.api.trakt.TraktApi;
import fr.raksrinana.rsndiscord.api.trakt.model.users.settings.UserSettings;
import fr.raksrinana.rsndiscord.api.trakt.requests.ITraktGetRequest;
import kong.unirest.GenericType;
import kong.unirest.GetRequest;
import kong.unirest.Unirest;
import org.jetbrains.annotations.NotNull;

public class UserSettingsGetRequest implements ITraktGetRequest<UserSettings>{
	@Override
	@NotNull
	public GenericType<UserSettings> getOutputType(){
		return new GenericType<>(){};
	}
	
	@Override
	@NotNull
	public GetRequest getRequest(){
		return Unirest.get(TraktApi.API_URL + "/users/settings");
	}
}
