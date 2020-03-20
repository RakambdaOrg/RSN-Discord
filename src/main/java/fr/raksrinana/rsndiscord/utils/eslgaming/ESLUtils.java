package fr.raksrinana.rsndiscord.utils.eslgaming;

import fr.raksrinana.rsndiscord.utils.RequestException;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.utils.http.requestssenders.get.ObjectGetRequestSender;
import lombok.NonNull;
import java.util.Map;

public class ESLUtils{
	public static final String API_URL = "https://cdn1.api.esl.tv/v1";
	private static final Map<String, String> HEADERS = Map.of("Content-Type", "application/json");
	
	public static <T> T getQuery(@NonNull ESLGetRequest<T> request) throws RequestException{
		final var handler = new ObjectGetRequestSender<>(request.getOutputType(), request.getRequest().headers(HEADERS)).getRequestHandler();
		handler.getResult().getParsingError().ifPresent(error -> Log.getLogger(null).warn("Failed to parse ESL response", error));
		if(handler.getResult().isSuccess() && request.isValidResult(handler.getStatus())){
			return handler.getRequestResult();
		}
		throw new RequestException("Error sending API request, HTTP code " + handler.getStatus() + " => " + handler.getRequestResult().toString(), handler.getStatus());
	}
}
