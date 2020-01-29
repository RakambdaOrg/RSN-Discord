package fr.raksrinana.rsndiscord.utils.themoviedb;

import fr.raksrinana.rsndiscord.utils.RequestException;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.utils.http.requestssenders.get.ObjectGetRequestSender;
import lombok.NonNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TMDBUtils{
	public static final String API_URL = "https://api.themoviedb.org/3";
	private static String ACCESS_TOKEN;
	
	public static <T> T getQuery(@NonNull TMDBGetRequest<T> request) throws RequestException{
		final var handler = new ObjectGetRequestSender<>(request.getOutputType(), request.getRequest().headers(getHeaders())).getRequestHandler();
		handler.getResult().getParsingError().ifPresent(error -> Log.getLogger(null).warn("Failed to parse response", error));
		if(request.isValidResult(handler.getStatus())){
			return handler.getRequestResult();
		}
		throw new RequestException("Error sending API request, HTTP code " + handler.getStatus() + " => " + handler.getRequestResult().toString(), handler.getStatus());
	}
	
	public static URL getImageURL(@NonNull String path, @NonNull String size){
		if(!path.startsWith("/")){
			path = "/" + path;
		}
		try{
			return new URL(MessageFormat.format("https://image.tmdb.org/t/p/{0}{1}", size, path));
		}
		catch(MalformedURLException e){
			Log.getLogger(null).error("Failed to generate image url of size {} for path {}", size, path, e);
		}
		return null;
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
