package fr.raksrinana.rsndiscord.modules.cat;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.cat.data.Cat;
import fr.raksrinana.rsndiscord.utils.RequestException;
import fr.raksrinana.rsndiscord.utils.Utilities;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.Objects.isNull;

public class TheCatApi{
	public static final String API_URL = "https://api.thecatapi.com/v1";
	private static String accessToken;
	
	private static List<Cat> getRandomCats() throws RequestException{
		var request = Unirest.get(API_URL + "/images/search")
				.headers(getHeaders())
				.queryString("order", "RANDOM")
				.asObject(new GenericType<List<Cat>>(){});
		
		request.getParsingError().ifPresent(error -> {
			Utilities.reportException("Failed to parse TheCatAPI response", error);
			Log.getLogger(null).warn("Failed to parse TheCatAPI response", error);
		});
		
		var status = request.getStatus();
		if(status == 200){
			return request.getBody();
		}
		throw new RequestException("Error sending API request, HTTP code " + status + " => " + request.getBody(), status);
	}
	
	private static Map<String, String> getHeaders(){
		var headers = new HashMap<String, String>();
		headers.put("x-api-key", getAccessToken());
		return headers;
	}
	
	public static String getAccessToken(){
		if(isNull(accessToken)){
			accessToken = System.getProperty("THE_CAT_API_TOKEN");
		}
		return accessToken;
	}
	
	public static Optional<Cat> getRandomCat(){
		try{
			return getRandomCats().stream().findFirst();
		}
		catch(RequestException e){
			Log.getLogger(null).error("Failed to get a random cat", e);
		}
		return Optional.empty();
	}
}
