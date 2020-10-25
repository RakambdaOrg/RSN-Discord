package fr.raksrinana.rsndiscord.modules.series.trakt.requests.users;

import fr.raksrinana.rsndiscord.modules.series.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.modules.series.trakt.model.users.settings.UserSettings;
import fr.raksrinana.rsndiscord.modules.series.trakt.requests.ITraktGetRequest;
import kong.unirest.GenericType;
import kong.unirest.GetRequest;
import kong.unirest.Unirest;

public class UserSettingsGetRequest implements ITraktGetRequest<UserSettings>{
	@Override
	public GenericType<UserSettings> getOutputType(){
		return new GenericType<>(){};
	}
	
	@Override
	public GetRequest getRequest(){
		return Unirest.get(TraktUtils.API_URL + "/users/settings");
	}
}
