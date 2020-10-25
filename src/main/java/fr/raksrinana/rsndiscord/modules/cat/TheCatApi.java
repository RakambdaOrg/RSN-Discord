package fr.raksrinana.rsndiscord.modules.cat;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.cat.data.Cat;
import fr.raksrinana.rsndiscord.utils.RequestException;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.utils.http.requestssenders.get.ObjectGetRequestSender;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import java.util.*;

public class TheCatApi{
	public static final String API_URL = "https://api.thecatapi.com/v1";
	private static String accessToken;
	
	private static List<Cat> getRandomCats() throws RequestException{
		final var handler = new ObjectGetRequestSender<>(new GenericType<List<Cat>>(){}, Unirest.get(API_URL + "/images/search")
				.headers(getHeaders())
				.queryString("order", "RANDOM")).getRequestHandler();
		handler.getResult().getParsingError().ifPresent(error -> {
			Utilities.reportException("Failed to parse TheCatAPI response", error);
			Log.getLogger(null).warn("Failed to parse TheCatAPI response", error);
		});
		if(handler.getStatus() == 200){
			return handler.getRequestResult();
		}
		throw new RequestException("Error sending API request, HTTP code " + handler.getStatus() + " => " + handler.getRequestResult().toString(), handler.getStatus());
	}
	
	private static Map<String, String> getHeaders(){
		final var headers = new HashMap<String, String>();
		headers.put("x-api-key", getAccessToken());
		return headers;
	}
	
	public static String getAccessToken(){
		if(Objects.isNull(accessToken)){
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
