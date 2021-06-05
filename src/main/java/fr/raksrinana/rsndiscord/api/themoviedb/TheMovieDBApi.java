package fr.raksrinana.rsndiscord.api.themoviedb;

import fr.raksrinana.rsndiscord.api.themoviedb.requests.ITMDBGetRequest;
import fr.raksrinana.rsndiscord.utils.RequestException;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import static java.util.Objects.isNull;

@Log4j2
public class TheMovieDBApi{
	public static final String API_URL = "https://api.themoviedb.org/3";
	private static String accessToken;
	
	@NotNull
	public static <T> T getQuery(@NotNull ITMDBGetRequest<T> tmdbGetRequest) throws RequestException{
		var request = tmdbGetRequest.getRequest().headers(getHeaders()).asObject(tmdbGetRequest.getOutputType());
		request.getParsingError().ifPresent(error -> {
			Utilities.reportException("Failed to parse TMDB response", error);
			log.warn("Failed to parse TMDB response", error);
		});
		if(!request.isSuccess()){
			throw new RequestException("Error sending API request, HTTP code " + request.getStatus() + " => " + request.getBody(), request.getStatus());
		}
		return request.getBody();
	}
	
	@NotNull
	private static Map<String, String> getHeaders(){
		var headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + getAccessToken());
		headers.put("Content-Type", "application/json");
		return headers;
	}
	
	@NotNull
	public static String getAccessToken(){
		if(isNull(accessToken)){
			accessToken = System.getProperty("TMDB_ACCESS_TOKEN");
		}
		return accessToken;
	}
	
	@Nullable
	public static URL getImageURL(@NotNull String path, @NotNull String size){
		if(!path.startsWith("/")){
			path = "/" + path;
		}
		try{
			return new URL("https://image.tmdb.org/t/p/%s%s".formatted(size, path));
		}
		catch(MalformedURLException e){
			log.error("Failed to generate image url of size {} for path {}", size, path, e);
		}
		return null;
	}
}
