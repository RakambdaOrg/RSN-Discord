package fr.raksrinana.rsndiscord.api.thecatapi;

import fr.raksrinana.rsndiscord.api.thecatapi.data.Cat;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.RequestException;
import fr.raksrinana.rsndiscord.utils.Utilities;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.Objects.isNull;
import static java.util.Optional.empty;

public class TheCatApi{
	public static final String API_URL = "https://api.thecatapi.com/v1";
	private static String accessToken;
	
	@NotNull
	public static Optional<Cat> getRandomCat(){
		try{
			return getRandomCats().stream().findFirst();
		}
		catch(RequestException e){
			Log.getLogger().error("Failed to get a random cat", e);
		}
		return empty();
	}
	
	@NotNull
	private static List<Cat> getRandomCats() throws RequestException{
		var request = Unirest.get(API_URL + "/images/search")
				.headers(getHeaders())
				.queryString("order", "RANDOM")
				.asObject(new GenericType<List<Cat>>(){});
		
		request.getParsingError().ifPresent(error -> {
			Utilities.reportException("Failed to parse TheCatAPI response", error);
			Log.getLogger().warn("Failed to parse TheCatAPI response", error);
		});
		
		if(!request.isSuccess()){
			var status = request.getStatus();
			throw new RequestException("Error sending API request, HTTP code " + status + " => " + request.getBody(), status);
		}
		return request.getBody();
	}
	
	@NotNull
	private static Map<String, String> getHeaders(){
		var headers = new HashMap<String, String>();
		headers.put("x-api-key", getAccessToken());
		return headers;
	}
	
	@Nullable
	public static String getAccessToken(){
		if(isNull(accessToken)){
			accessToken = System.getProperty("THE_CAT_API_TOKEN");
		}
		return accessToken;
	}
}
