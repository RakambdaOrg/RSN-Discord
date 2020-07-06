package fr.raksrinana.rsndiscord.utils.externaltodos;

import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.utils.http.requestssenders.get.ObjectGetRequestSender;
import fr.raksrinana.utils.http.requestssenders.post.ObjectPostRequestSender;
import kong.unirest.GenericType;
import kong.unirest.HeaderNames;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import java.util.Optional;

public class ExternalTodosUtils{
	public static Optional<GetTodoResponse> getTodos(String endpoint, String token){
		final var request = Unirest.get(endpoint + "/todos");
		Optional.ofNullable(token).ifPresent(t -> request.header(HeaderNames.AUTHORIZATION, "Bearer " + t));
		final var handler = new ObjectGetRequestSender<GetTodoResponse>(new GenericType<>(){}, request).getRequestHandler();
		handler.getResult().getParsingError().ifPresent(error -> {
			Utilities.reportException("Failed to parse external todos response", error);
			Log.getLogger(null).warn("Failed to parse external todos response", error);
		});
		if(handler.getResult().isSuccess() && handler.getRequestResult().getCode() == 200){
			return Optional.of(handler.getRequestResult());
		}
		return Optional.empty();
	}
	
	public static Optional<SetStatusResponse> setStatus(String endpoint, String token, Todo todo, Status status){
		final var body = new JSONObject();
		body.put("id", todo.getId());
		body.put("status", status.name());
		final var request = Unirest.post(endpoint + "/todos/status/set").body(body);
		Optional.ofNullable(token).ifPresent(t -> request.header(HeaderNames.AUTHORIZATION, "Bearer " + t));
		final var handler = new ObjectPostRequestSender<SetStatusResponse>(new GenericType<>(){}, request).getRequestHandler();
		handler.getResult().getParsingError().ifPresent(error -> {
			Utilities.reportException("Failed to parse external todos response", error);
			Log.getLogger(null).warn("Failed to parse external todos response", error);
		});
		if(handler.getResult().isSuccess() && handler.getRequestResult().getCode() == 200){
			return Optional.of(handler.getRequestResult());
		}
		return Optional.empty();
	}
}
