package fr.raksrinana.rsndiscord.utils.trakt.requests.users;

import fr.raksrinana.rsndiscord.utils.trakt.TraktGetRequest;
import fr.raksrinana.rsndiscord.utils.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.utils.trakt.model.users.settings.UserSettings;
import kong.unirest.GenericType;
import kong.unirest.GetRequest;
import kong.unirest.Unirest;

public class UserSettingsGetRequest implements TraktGetRequest<UserSettings>{
	@Override
	public GenericType<UserSettings> getOutputType(){
		return new GenericType<>(){};
	}
	
	@Override
	public GetRequest getRequest(){
		return Unirest.get(TraktUtils.API_URL + "/users/settings");
	}
}
