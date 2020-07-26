package fr.raksrinana.rsndiscord.utils.giphy;

import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.utils.http.requestssenders.get.ObjectGetRequestSender;
import java.util.Objects;
import java.util.Optional;

public class GiphyUtils{
	public static final String API_URL = "https://api.giphy.com/v1";
	private static String apiKey;
	
	public static <T> Optional<T> getRequest(GiphyGetRequest<T> request){
		Log.getLogger(null).debug("Requesting Giphy");
		final var handler = new ObjectGetRequestSender<>(request.getOutputType(), request.getRequest().queryString("api_key", getApiKey())).getRequestHandler();
		handler.getResult().getParsingError().ifPresent(error -> {
			Utilities.reportException("Failed to parse Giphy response", error);
			Log.getLogger(null).warn("Failed to parse Giphy response", error);
		});
		if(handler.getStatus() == 200){
			return Optional.ofNullable(handler.getRequestResult());
		}
		return Optional.empty();
	}
	
	private static String getApiKey(){
		if(Objects.isNull(apiKey)){
			apiKey = System.getProperty("GIPHY_API_KEY");
		}
		return apiKey;
	}
}
