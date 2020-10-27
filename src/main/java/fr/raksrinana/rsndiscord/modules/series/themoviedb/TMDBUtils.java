package fr.raksrinana.rsndiscord.modules.series.themoviedb;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.series.themoviedb.requests.ITMDBGetRequest;
import fr.raksrinana.rsndiscord.utils.RequestException;
import fr.raksrinana.rsndiscord.utils.Utilities;
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
	private static String accessToken;
	
	public static <T> T getQuery(@NonNull ITMDBGetRequest<T> request) throws RequestException{
		final var handler = new ObjectGetRequestSender<>(request.getOutputType(), request.getRequest().headers(getHeaders())).getRequestHandler();
		handler.getResult().getParsingError().ifPresent(error -> {
			Utilities.reportException("Failed to parse TMDB response", error);
			Log.getLogger(null).warn("Failed to parse TMDB response", error);
		});
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
		if(Objects.isNull(accessToken)){
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