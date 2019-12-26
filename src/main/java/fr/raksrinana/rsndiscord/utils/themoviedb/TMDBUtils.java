package fr.raksrinana.rsndiscord.utils.themoviedb;

import fr.raksrinana.rsndiscord.utils.RequestException;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.utils.http.requestssenders.get.ObjectGetRequestSender;
import lombok.NonNull;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class TMDBUtils{
	private static final String API_URL = "https://api.themoviedb.org/3";
	private static String ACCESS_TOKEN;
	
	public static <T> T getQuery(@NonNull TMDBGetRequest<T> request) throws RequestException, MalformedURLException, URISyntaxException{
		final var headers = getHeaders();
		final var parameters = Optional.ofNullable(request.getParameters()).orElseGet(HashMap::new);
		final var handler = new ObjectGetRequestSender<>(request.getResultClass(), new URL(API_URL + request.getEndpoint()), headers, parameters).getRequestHandler();
		handler.getResult().getParsingError().ifPresent(error -> Log.getLogger(null).warn("Failed to parse response", error));
		if(request.isValidResult(handler.getStatus())){
			return handler.getRequestResult();
		}
		throw new RequestException("Error sending API request, HTTP code " + handler.getStatus() + " => " + handler.getRequestResult().toString(), handler.getStatus());
	}
	
	private static Map<String, String> getHeaders(){
		final var headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + getAccessToken());
		headers.put("Content-Type", "application/json");
		return headers;
	}
	
	public static String getAccessToken(){
		if(Objects.isNull(ACCESS_TOKEN)){
			ACCESS_TOKEN = System.getProperty("TMDB_ACCESS_TOKEN");
		}
		return ACCESS_TOKEN;
	}
}
