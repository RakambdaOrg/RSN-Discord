package fr.raksrinana.rsndiscord.api.themoviedb;

import fr.raksrinana.rsndiscord.api.themoviedb.requests.ITMDBGetRequest;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.RequestException;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import static java.util.Objects.isNull;

public class TheMovieDBApi{
	public static final String API_URL = "https://api.themoviedb.org/3";
	private static String accessToken;
	
	public static <T> T getQuery(@NonNull ITMDBGetRequest<T> tmdbGetRequest) throws RequestException{
		final var request = tmdbGetRequest.getRequest().headers(getHeaders()).asObject(tmdbGetRequest.getOutputType());
		request.getParsingError().ifPresent(error -> {
			Utilities.reportException("Failed to parse TMDB response", error);
			Log.getLogger(null).warn("Failed to parse TMDB response", error);
		});
		if(tmdbGetRequest.isValidResult(request.getStatus())){
			return request.getBody();
		}
		throw new RequestException("Error sending API request, HTTP code " + request.getStatus() + " => " + request.getBody(), request.getStatus());
	}
	
	private static Map<String, String> getHeaders(){
		final var headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + getAccessToken());
		headers.put("Content-Type", "application/json");
		return headers;
	}
	
	public static String getAccessToken(){
		if(isNull(accessToken)){
			accessToken = System.getProperty("TMDB_ACCESS_TOKEN");
		}
		return accessToken;
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
}
